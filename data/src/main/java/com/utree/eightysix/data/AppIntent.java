/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
* Created by Administrator on 2015/1/30 0030.
*/
public class AppIntent {

  /**
   * 1000 push
   * 1001 ad
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
}
