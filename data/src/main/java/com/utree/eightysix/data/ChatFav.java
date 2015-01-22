/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class ChatFav {

  @SerializedName("chatId")
  public String chatId;

  @SerializedName("bgUrl")
  public String bgUrl;

  @SerializedName("bgColor")
  public String bgColor;

  @SerializedName("postId")
  public String postId;

  @SerializedName("postContent")
  public String postContent;

  @SerializedName("commentId")
  public String commentId;

  @SerializedName("commentContent")
  public String commentContent;

  @SerializedName("factoryName")
  public String factoryName;

  @SerializedName("relation")
  public String relation;

  public String targetAvatar;

  public String myAvatar;
}
