/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class ChatFav {

  @SerializedName("userAvatar")
  public String userAvatar;

  @SerializedName("bgUrl")
  public String bgUrl;

  @SerializedName("postId")
  public String postId;

  @SerializedName("commentId")
  public String commentId;

  @SerializedName("chatSource")
  public String chatSource;

  @SerializedName("relation")
  public String relation;

}
