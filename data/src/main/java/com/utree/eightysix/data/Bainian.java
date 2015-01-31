/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 */
public class Bainian extends BaseItem {


  @SerializedName("title")
  public String title;

  @SerializedName("subTitle")
  public String subTitle;

  @SerializedName("receiveText")
  public String receiveText;

  @SerializedName("contentText")
  public String contentText;

  @SerializedName("buttonText")
  public String buttonText;

  @SerializedName("newYearContents")
  public List<NewYearContent> newYearContents;

  public static class NewYearContent {

    @SerializedName("id")
    public int id;

    @SerializedName("content")
    public String content;
  }
}
