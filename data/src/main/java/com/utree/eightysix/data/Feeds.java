package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Feeds {

  @SerializedName ("myPraiseCount")
  public int myPraiseCount;

  @SerializedName ("factoryView")
  public Circle circle;

  /**
   * 是否被锁定
   * <p/>
   * 1 锁定
   * 0 不锁定
   */
  @SerializedName ("lock")
  public int lock;

  /**
   * 赞百分比
   */
  @SerializedName ("praisePercent")
  public String praisePercent;

  /**
   * 1 上升
   * 0 不变
   * -1 下降
   */
  @SerializedName ("upDown")
  public int upDown;

  /**
   * 是否是用户当前工厂
   * <p/>
   * 1 是
   * 0 不是
   */
  @SerializedName ("current")
  public int current;

  /**
   * 圈子朋友数
   */
  @SerializedName ("currFactoryFriends")
  public int currFactoryFriends;

  /**
   * 被隐藏的帖子数
   */
  @SerializedName ("friendAnonymousPostCount")
  public int hiddenCount;

  /**
   * 是否上传通讯录
   * <p/>
   * 1 已上传
   * 0 未选择
   */
  @SerializedName ("upContact")
  public int upContact;

  @SerializedName("fetchNoticeView")
  public FetchNotification fetch;

  /**
   * 是否选择工厂
   * <p/>
   * 1 已选择
   * 0 未选择
   */
  @SerializedName ("selectFactory")
  public int selectFactory;

  @SerializedName ("posts")
  public Paginate<BaseItem> posts;

  @SerializedName ("workerCount")
  public int workerCount;

  @SerializedName("subInfo")
  public String subInfo;

  /**
   * 是否有设置hometown
   * 1 已设置
   * 0 未设置
   */
  @SerializedName("hometown")
  public int hometown;

  @SerializedName("hometownId")
  public int hometownId;

  @SerializedName("hometownType")
  public int hometownType;

  @SerializedName("hometownName")
  public String hometownName;
}
