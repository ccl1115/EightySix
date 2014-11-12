package com.utree.eightysix.ob;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class OssBatch {

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

  List<String> mOverride;

  OSSClient mOSSClient =
      new OSSClient(C.ENDPOINT, C.ACCESS_KEY_ID, C.ACCESS_KEY_SECRET);

  public OssBatch() {
    mLogger = Logger.getLogger("oss-batch");
    try {
      mLogger.addHandler(new FileHandler("log"));
    } catch (IOException e) {
      System.exit(1);
    }
  }

  private void readLocalOverride() {
    mOverride = new ArrayList<String>();
    try {
      mOverride = FileUtils.readLines(new File("override"));
    } catch (IOException e) {
      System.exit(1);
    }
  }

  private void readRemoteList() {
    ListObjectsRequest request = new ListObjectsRequest(C.BUCKET, "", "", "", 500);
  }

  public void batch() {

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType("image/jpg");

    for (String p : PREFIX) {
      for (String s : SUB_PREFIX) {
        mLogger.log(Level.INFO, String.format("start get list with prefix: /%s/%s", p, s));
        ObjectListing objectListing = mOSSClient.listObjects(C.BUCKET, p + "/" + s);
        List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        mLogger.log(Level.INFO, String.format("get %d objects with prefix /%s/%s", objectSummaries.size(), p, s));

        for (OSSObjectSummary summary : objectSummaries) {
          CopyObjectRequest r = new CopyObjectRequest(C.BUCKET, summary.getKey(), C.BUCKET, summary.getKey());
          r.setNewObjectMetadata(objectMetadata);

          mOSSClient.copyObject(r);

          mLogger.log(Level.INFO, String.format("object %s is updated", summary.getKey()));

          try {
            synchronized (this) {
              wait(500);
            }
          } catch (InterruptedException ignored) {
          }
        }

        try {
          synchronized (this) {
            wait(2000);
          }
        } catch (InterruptedException ignored) {
        }
      }
    }
  }

  public void update(OSSObject ossObject) {
  }

  public static int main(String[] args) {
    new OssBatch().batch();
    return 0;
  }
}
