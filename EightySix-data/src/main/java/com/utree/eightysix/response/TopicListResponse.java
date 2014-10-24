package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Topics;
import com.utree.eightysix.rest.Response;

/**
 */
public class TopicListResponse extends Response {

  @SerializedName("object")
  public Topics object;
}
