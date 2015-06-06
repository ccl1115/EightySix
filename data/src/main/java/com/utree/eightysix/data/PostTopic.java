package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 */
public class PostTopic extends BaseItem {

  @SerializedName("headTitle")
  public String headTitle;

  @SerializedName("title")
  public String title;

  @SerializedName("tags")
  public List<Tag> tags;

  @SerializedName("postCount")
  public int postCount;

  @SerializedName("id")
  public int id;

  @SerializedName("topicHit")
  public String hint;
}
