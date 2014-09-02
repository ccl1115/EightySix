package com.utree.eightysix.rest;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class ContactsSyncResponse extends Response {

  @SerializedName("object")
  public ContactsSync object;

  public static class ContactsSync {
    @SerializedName("friendCount")
    public int friendCount;

    @SerializedName("workerCount")
    public int workerCount;
  }
}
