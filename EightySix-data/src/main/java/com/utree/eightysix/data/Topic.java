package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 */
public class Topic {

  @SerializedName("id")
  public String id;

  @SerializedName("content")
  public String content;

  @SerializedName("tags")
  public List<Tag> tags;

  @SerializedName("postCount")
  public int postCount;
}
