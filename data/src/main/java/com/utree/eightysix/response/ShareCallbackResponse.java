package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class ShareCallbackResponse extends Response {

  @SerializedName("object")
  public ShareCallback object;

  public static class ShareCallback {

    @SerializedName("experience")
    public int experience;
  }
}
