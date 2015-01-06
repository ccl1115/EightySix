package com.utree.eightysix.ob;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.*;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class OssBatchDownload {

  public static final String[] PREFIX = {
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
  };

  public static final String[] SUB_PREFIX = {
      "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e", "0f",
      "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b", "1c", "1d", "1e", "1f",
      "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f",
      "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c", "3d", "3e", "3f",
      "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f",
      "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5a", "5b", "5c", "5d", "5e", "5f",
      "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6a", "6b", "6c", "6d", "6e", "6f",
      "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7a", "7b", "7c", "7d", "7e", "7f",
      "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8a", "8b", "8c", "8d", "8e", "8f",
      "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9a", "9b", "9c", "9d", "9e", "9f",
      "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa", "ab", "ac", "ad", "ae", "af",
      "b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf",
      "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb", "cc", "cd", "ce", "cf",
      "d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df",
      "e0", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef",
      "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff"
  };

  Logger mLogger;

  List<String> mDownloaded;
  private List<String> mRemoteList;

  OSSClient mOSSClient =
      new OSSClient(C.ENDPOINT, C.ACCESS_KEY_ID, C.ACCESS_KEY_SECRET);

  public OssBatchDownload() {
    mLogger = Logger.getLogger("oss-batch");
    mLogger.setLevel(Level.ALL);
    try {
      mLogger.addHandler(new FileHandler("log"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    initDirs();
    readLocalOverride();
    readRemoteObjectsList();
  }

  private void initDirs() {
    for (String s : PREFIX) {
      File file = new File(s);
      if (!file.exists()) {
        file.mkdirs();
      }
    }
  }

  private void readLocalOverride() {
    mDownloaded = new ArrayList<String>();
    try {
      mDownloaded = Files.readLines(new File("downloaded"), Charset.defaultCharset());
    } catch (FileNotFoundException ignored) {
    } catch (IOException e) {
      System.exit(1);
    }
    mLogger.log(Level.INFO, String.format("get %d items from local", mDownloaded.size()));
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
      //return null;
    }
  }

  public void batch() {
    TreeSet<String> set1 = new TreeSet<String>(mDownloaded);
    TreeSet<String> set2 = new TreeSet<String>(mRemoteList);

    mLogger.log(Level.INFO, String.format("%d items in local", set1.size()));
    mLogger.log(Level.INFO, String.format("%d items in remote", set2.size()));

    Set<String> toBeConvert = Sets.difference(set2, set1);

    mLogger.log(Level.INFO, String.format("%d items to be update", toBeConvert.size()));

    int index = 0;
    for (String s : toBeConvert) {
      if (index > 300) break;
      GetObjectRequest request = new GetObjectRequest(C.BUCKET, s);

      FileOutputStream output = null;
      try {
        OSSObject ossObject = mOSSClient.getObject(request);
        String path = s.substring(0, 3) + s.substring(5);
        File file = new File(path + ".jpg");
        output = new FileOutputStream(file);
        IOUtils.copy(ossObject.getObjectContent(), output);
        mDownloaded.add(s);
      } catch (Throwable e) {
        mLogger.log(Level.WARNING, String.format("failed to update %s", s));
      } finally {
        if (output != null) {
          try {
            output.close();
          } catch (IOException ignored) {
            ignored.printStackTrace();
          }
        }
      }

      mLogger.log(Level.INFO, String.format("updated %s", s));
      mLogger.log(Level.INFO, String.format("completed: %f%%", mDownloaded.size() * 100f / mRemoteList.size()));

      index ++;

      try {
        synchronized (this) {
          wait(500);
        }
      } catch (InterruptedException ignored) {
        ignored.printStackTrace();
      }
    }

    mLogger.log(Level.INFO, String.format("downloaded files %s", mDownloaded.size()));

    BufferedWriter writer = null;
    try {
      writer = Files.newWriter(new File("downloaded"), Charset.defaultCharset());
      for (String item : mDownloaded) {
        writer.write(item);
        writer.write('\n');
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(-1);
    } catch (IOException ignored) {
      ignored.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.flush();
          writer.close();
        } catch (IOException ignored) {
          ignored.printStackTrace();
        }
      }
    }
  }

  public static int main(String[] args) {
    new OssBatchDownload().batch();
    return 0;
  }
}
