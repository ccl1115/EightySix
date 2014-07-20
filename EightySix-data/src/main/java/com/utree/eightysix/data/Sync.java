package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class Sync {

  @SerializedName("upgrade")
  public Upgrade upgrade;

  @SerializedName("selectFactoryDays")
  public int selectFactoryDays;

  @SerializedName("unLockFriends")
  public int unlockFriends;

  @SerializedName("portraite")
  public Portrait portrait;

  public static class Portrait {
    @SerializedName("url")
    public String url;

    @SerializedName("version")
    public String version;

    @Override
    public String toString() {
      return "Portrait{" +
          "url='" + url + '\'' +
          ", version='" + version + '\'' +
          '}';
    }
  }

  public static class Upgrade {
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

    @Override
    public String toString() {
      return "Upgrade{" +
          "url='" + url + '\'' +
          ", force=" + force +
          ", info='" + info + '\'' +
          ", version='" + version + '\'' +
          ", remind=" + remind +
          '}';
    }
  }

  @Override
  public String toString() {
    return "Sync{" +
        "upgrade=" + upgrade +
        ", selectFactoryDays=" + selectFactoryDays +
        ", unlockFriends=" + unlockFriends +
        ", portrait=" + portrait +
        '}';
  }
}
