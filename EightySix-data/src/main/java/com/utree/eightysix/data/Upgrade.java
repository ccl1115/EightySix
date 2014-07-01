package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class Upgrade {

  /**
   * 下载链接
   */
  @SerializedName("url")
  public String url;

  /**
   * 是否强制升级
   */
  @SerializedName("force")
  public int force;

  /**
   * 升级信息
   */
  @SerializedName("info")
  public String info;

  /**
   * 升级版本号
   */
  @SerializedName("version")
  public String version;

  /**
   * 是否提醒
   */
  @SerializedName("remind")
  public int remind;
}
