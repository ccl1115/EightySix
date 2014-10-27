package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class TopicFeed {

  @SerializedName ("topic")
  public Topic topic;

  @SerializedName ("posts")
  public Paginate<Post> posts;
}
