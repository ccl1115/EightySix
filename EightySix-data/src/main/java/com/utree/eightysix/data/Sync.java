package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
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

  public static class Portrait implements Parcelable {


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

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.url);
      dest.writeString(this.version);
    }

    public Portrait() {
    }

    private Portrait(Parcel in) {
      this.url = in.readString();
      this.version = in.readString();
    }

    public static final Parcelable.Creator<Portrait> CREATOR = new Parcelable.Creator<Portrait>() {
      public Portrait createFromParcel(Parcel source) {
        return new Portrait(source);
      }

      public Portrait[] newArray(int size) {
        return new Portrait[size];
      }
    };
  }

  public static class Upgrade implements Parcelable {

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

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.url);
      dest.writeInt(this.force);
      dest.writeString(this.info);
      dest.writeString(this.version);
      dest.writeInt(this.remind);
    }

    public Upgrade() {
    }

    private Upgrade(Parcel in) {
      this.url = in.readString();
      this.force = in.readInt();
      this.info = in.readString();
      this.version = in.readString();
      this.remind = in.readInt();
    }

    public static final Parcelable.Creator<Upgrade> CREATOR = new Parcelable.Creator<Upgrade>() {
      public Upgrade createFromParcel(Parcel source) {
        return new Upgrade(source);
      }

      public Upgrade[] newArray(int size) {
        return new Upgrade[size];
      }
    };
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
