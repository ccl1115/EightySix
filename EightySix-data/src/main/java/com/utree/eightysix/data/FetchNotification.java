package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class FetchNotification {

  @SerializedName("newPraise")
  public PullNotification newPraise;

  @SerializedName("newComment")
  public PullNotification newComment;

  @SerializedName("myPostComment")
  public PullNotification myPostComment;

  @SerializedName("newPost")
  public PullNotification newPost;

  @SerializedName("newFactoryPass")
  public PullNotification newFactoryUnlock;

  @SerializedName("friendL1Join")
  public PullNotification friendL1Join;

  @SerializedName("newPostAllCount")
  public int newPostAllCount;

  @SerializedName("newPostHotCount")
  public int newPostHotCount;

  @SerializedName("newPostFriendsCount")
  public int newPostFriendsCount;
}
