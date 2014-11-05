package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class TagFeeds {

  @SerializedName ("posts")
  public Paginate<BaseItem> posts;

  @SerializedName ("factoryView")
  public Circle circle;
}
