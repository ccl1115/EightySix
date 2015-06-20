/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.event;

/**
 * Fire when PullNotificationService fetch new comment count
 *
 * 我发表的帖子收到的评论
 *
 * @author simon
 */
public class MyPostCommentCountEvent {
  private int mCount;

  public MyPostCommentCountEvent(int count) {
    mCount = count;
  }

  public int getCount() {
    return mCount;
  }
}
