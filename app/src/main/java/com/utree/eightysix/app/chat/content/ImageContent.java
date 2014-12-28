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

  @SerializedName("localThumb")
  public String localThumb;

  @SerializedName("remote")
  public String remote;

  @SerializedName("thumbnail")
  public String thumbnail;

  @SerializedName("secret")
  public String secret;

  public ImageContent(String local, String remote, String secret, String localThumb, String thumbnail) {
    this.local = local;
    this.remote = remote;
    this.secret = secret;
    this.localThumb = localThumb;
    this.thumbnail = thumbnail;
  }
}
