package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.TopicFeed;
import com.utree.eightysix.rest.Response;

/**
 */
public class TopicFeedResponse extends Response {

  @SerializedName ("object")
  public TopicFeed object;
}
