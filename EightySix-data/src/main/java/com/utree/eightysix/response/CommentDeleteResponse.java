package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class CommentDeleteResponse extends Response {

  @SerializedName("object")
  public Reason object;

  public static class Reason {

    @SerializedName("reason")
    public String reason;

  }
}
