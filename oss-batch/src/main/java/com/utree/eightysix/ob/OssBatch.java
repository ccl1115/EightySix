package com.utree.eightysix.ob;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class OssBatch {

  Logger mLogger;

  List<String> mOverride;
  private List<String> mRemoteList;

  OSSClient mOSSClient =
      new OSSClient(C.ENDPOINT, C.ACCESS_KEY_ID, C.ACCESS_KEY_SECRET);

  public OssBatch() {
    mLogger = Logger.getLogger("oss-batch");
    mLogger.setLevel(Level.ALL);
    try {
      mLogger.addHandler(new FileHandler("log"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    readLocalOverride();
    readRemoteObjectsList();
  }

  private void readLocalOverride() {
    mOverride = new ArrayList<String>();
    try {
      mOverride = Files.readLines(new File("override"), Charset.defaultCharset());
    } catch (FileNotFoundException ignored) {
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    mLogger.log(Level.INFO, String.format("get %d items from local", mOverride.size()));
  }

  private void readRemoteObjectsList() {
    mRemoteList = new ArrayList<String>();
    String marker = "";
    do {
      marker = requestPage(marker);
      mLogger.log(Level.INFO, "marker: " + marker);
      try {
        synchronized (this) {
          wait(500);
        }
      } catch (InterruptedException ignored) {
      }
    } while(marker != null && !marker.isEmpty());
    mLogger.log(Level.INFO, String.format("get %d items from remote", mRemoteList.size()));
  }

  private String requestPage(String marker) {
    ListObjectsRequest request = new ListObjectsRequest(C.BUCKET, "", marker, "", 300);
    ObjectListing objectListing = mOSSClient.listObjects(request);
    List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
    if (objectSummaries.size() == 0) {
      mLogger.log(Level.INFO, String.format("get %d item from page", 0));
      return null;
    } else {
      for (OSSObjectSummary summary : objectSummaries) {
        mRemoteList.add(summary.getKey());
      }
      mLogger.log(Level.INFO, String.format("get %d item from page", objectSummaries.size()));
      return objectListing.getNextMarker();
    }
  }

  public void batch() {
    TreeSet<String> set1 = new TreeSet<String>(mOverride);
    TreeSet<String> set2 = new TreeSet<String>(mRemoteList);

    mLogger.log(Level.INFO, String.format("%d items in local", set1.size()));
    mLogger.log(Level.INFO, String.format("%d items in remote", set2.size()));

    Set<String> toBeConvert = Sets.difference(set2, set1);

    mLogger.log(Level.INFO, String.format("%d items to be update", toBeConvert.size()));

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType("image/jpg");

    int index = 0;
    for (String s : toBeConvert) {
      if (index > 1000) break;
      CopyObjectRequest request = new CopyObjectRequest(C.BUCKET, s, C.BUCKET, s);
      request.setNewObjectMetadata(objectMetadata);

      try {
        mOSSClient.copyObject(request);
        mOverride.add(s);
      } catch (Throwable e) {
        mLogger.log(Level.WARNING, String.format("failed to update %s", s));
      }

      mLogger.log(Level.INFO, String.format("updated %s", s));
      mLogger.log(Level.INFO, String.format("completed: %f%%", mOverride.size() * 100f / mRemoteList.size()));

      index ++;

      try {
        synchronized (this) {
          wait(500);
        }
      } catch (InterruptedException ignored) {
      }
    }


    BufferedWriter override = null;
    try {
      override = Files.newWriter(new File("override"), Charset.defaultCharset());
      for (String item : mOverride) {
        override.write(item);
        override.write('\n');
      }

    } catch (FileNotFoundException e) {
      System.exit(-1);
    } catch (IOException ignored) {
    } finally {
      if (override != null) {
        try {
          override.flush();
          override.close();
        } catch (IOException ignored) {
        }
      }
    }
  }

  public static int main(String[] args) {
    new OssBatch().batch();
    return 0;
  }
}
