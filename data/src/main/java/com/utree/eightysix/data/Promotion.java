package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class Promotion extends BaseItem implements Parcelable {

  @SerializedName ("activeUrl")
  public String activeUrl;

  @SerializedName ("title")
  public String title;

  @SerializedName ("activeName")
  public String activeName;

  @SerializedName ("activeRemark")
  public String activeRemark;

  @SerializedName ("activeStartTime")
  public String activeStartTime;

  @SerializedName("activeWebViewName")
  public String activeWebViewName;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.activeUrl);
    dest.writeString(this.title);
    dest.writeString(this.activeWebViewName);
    dest.writeString(this.activeName);
    dest.writeString(this.activeRemark);
    dest.writeString(this.activeStartTime);
    dest.writeString(this.bgUrl);
    dest.writeString(this.bgColor);
    dest.writeString(this.content);
    dest.writeInt(this.type);
  }

  public Promotion() {
  }

  private Promotion(Parcel in) {
    this.activeUrl = in.readString();
    this.title = in.readString();
    this.activeWebViewName = in.readString();
    this.activeName = in.readString();
    this.activeRemark = in.readString();
    this.activeStartTime = in.readString();
    this.bgUrl = in.readString();
    this.bgColor = in.readString();
    this.content = in.readString();
    this.type = in.readInt();
  }

  public static final Parcelable.Creator<Promotion> CREATOR = new Parcelable.Creator<Promotion>() {
    public Promotion createFromParcel(Parcel source) {
      return new Promotion(source);
    }

    public Promotion[] newArray(int size) {
      return new Promotion[size];
    }
  };
}
