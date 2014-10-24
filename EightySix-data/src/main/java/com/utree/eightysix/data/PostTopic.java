package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class PostTopic extends BaseItem {

  @SerializedName("headTitle")
  public String headTitle;

  @SerializedName("postTopic")
  public Topic postTopic;
}
