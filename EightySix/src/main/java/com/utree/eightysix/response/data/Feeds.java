package com.utree.eightysix.response.data;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.response.Paginate;

/**
 */
public class Feeds {
  @SerializedName ("friendAnonymousPostCount")
  public int friendsPosts;

  @SerializedName ("myPraiseCount")
  public int myPraiseCount;

  public int showUnlock;

  public int hiddenCount;

  @SerializedName ("posts")
  public Paginate<Post> posts;
}
