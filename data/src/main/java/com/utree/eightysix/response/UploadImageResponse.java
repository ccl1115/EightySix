/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class UploadImageResponse extends Response {

  @SerializedName("object")
  public ImageUrl object;

  public static class ImageUrl {
    @SerializedName("image_url")
    public String imageUrl;
  }
}
