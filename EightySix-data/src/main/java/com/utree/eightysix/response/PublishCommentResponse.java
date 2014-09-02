package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class PublishCommentResponse extends Response {

  @SerializedName("object")
  public Comment object;

}
