/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat.content;

import com.google.gson.annotations.SerializedName;

/**
 */
public class ImageContent {

  @SerializedName("local")
  public String local;

  @SerializedName("remote")
  public String remote;

  @SerializedName("thumbnail")
  public String thumbnail;

  public ImageContent(String local, String remote, String thumbnail) {
    this.local = local;
    this.remote = remote;
    this.thumbnail = thumbnail;
  }
}
