package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class BaseItem {

  public static final int TYPE_POST = 1001;
  public static final int TYPE_PROMOTION = 1002;
  public static final int TYPE_QUESTION_SET = 1003;
  public static final int TYPE_OPTION_SET = 1004;
  public static final int TYPE_TOPIC = 1005;
  public static final int TYPE_BAINIAN = 1006;

  /**
   * Entrance of snapshot
   */
  public static final int TYPE_FEED_INTENT = 1007;

  @SerializedName ("bgUrl")
  public String bgUrl;

  @SerializedName ("bgColor")
  public String bgColor;

  @SerializedName ("content")
  public String content;

  @SerializedName ("type")
  public int type;

  public BaseItem(int type) {
    this.type = type;
  }

  public BaseItem() {}

}
