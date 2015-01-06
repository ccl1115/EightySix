package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class PublishPostResponse extends Response {

  @SerializedName("object")
  public PublishPost object;

  public static class PublishPost {
    @SerializedName("id")
    public String id;
  }
}
