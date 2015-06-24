/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
*/
public class AppIntent implements Serializable {

  /**
   * 1000 push
   * 1001 ad
   * 1002 push text
   */
  @SerializedName("type")
  public int type;

  @SerializedName("pushFlag")
  public String pushFlag;

  /**
   * "feed:factoryId"
   */
  @SerializedName("cmd")
  public String cmd;

  @SerializedName ("title")
  public String title;

  @SerializedName ("content")
  public String content;
}
