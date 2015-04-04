/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class CopywritingResponse extends Response {

  @SerializedName("object")
  public Object object;

  public static class Object {

    @SerializedName("HowToGetExperience")
    public HowToGetExperience howToGetExperience;
  }

  public static class HowToGetExperience {

    @SerializedName("content")
    public String content;
  }
}
