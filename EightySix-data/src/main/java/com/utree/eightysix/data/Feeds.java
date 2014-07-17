package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Feeds {

  @SerializedName ("myPraiseCount")
  public int myPraiseCount;

  @SerializedName("factoryView")
  public Circle circle;

  /**
   * 是否被锁定
   *
   * 1 锁定
   * 0 不锁定
   */
  @SerializedName("lock")
  public int lock;

  /**
   * 是否是用户当前工厂
   *
   * 1 是
   * 0 不是
   */
  @SerializedName("current")
  public int current;

  /**
   * 圈子朋友数
   */
  @SerializedName("currFactoryGoodFriends")
  public int currFactoryFriends;

  /**
   * 被隐藏的帖子数
   */
  @SerializedName("friendAnonymousPostCount")
  public int hiddenCount;

  /**
   * 是否上传通讯录
   *
   * 1 已上传
   * 0 未选择
   */
  @SerializedName("upContact")
  public int upContact;

  /**
   * 是否选择工厂
   *
   * 1 已选择
   * 0 未选择
   */
  @SerializedName("selectFactory")
  public int selectFactory;

  @SerializedName ("posts")
  public Paginate<BaseItem> posts;
}
