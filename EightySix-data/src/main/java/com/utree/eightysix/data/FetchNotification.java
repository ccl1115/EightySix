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
}
