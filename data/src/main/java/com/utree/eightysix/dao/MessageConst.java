/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.dao;

/**
 */
public class MessageConst {

  public static final int DIRECTION_RECEIVE = 0x1000;
  public static final int DIRECTION_SEND = 0x1001;
  public static final int DIRECTION_NON = 0x1002;

  public static final int STATUS_CREATE = 0x1000;
  public static final int STATUS_IN_PROGRESS = 0x1001;
  public static final int STATUS_FAILED = 0x1002;
  public static final int STATUS_SUCCESS = 0x1003;


  /**
   * 文本
   */
  public static final int TYPE_TXT = 0x1000;

  /**
   * 图片
   */
  public static final int TYPE_IMAGE = 0x1001;

  /**
   * 居中信息
   */
  public static final int TYPE_INFO = 0x1002;

  /**
   * 帖子内容摘要
   */
  public static final int TYPE_POST = 0x1003;


  /**
   * 评论内容摘要
   */
  public static final int TYPE_COMMENT = 0x1004;

  /**
   * 时间戳
   */
  public static final int TYPE_TIMESTAMP = 0x1005;
}