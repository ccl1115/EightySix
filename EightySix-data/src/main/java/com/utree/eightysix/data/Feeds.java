package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Feeds {
  @SerializedName ("friendAnonymousPostCount")
  public int friendsPosts;

  @SerializedName ("myPraiseCount")
  public int myPraiseCount;

  public int showUnlock;

  @SerializedName("friendAnonymousPostCount")
  public int hiddenCount;

  @SerializedName ("posts")
  public Paginate<Post> posts;
}
