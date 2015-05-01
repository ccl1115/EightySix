/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.event;

/**
 * Fire when need to ask a list view or a page to refresh its content.
 */
public class RequireRefreshEvent {
  public static final int REQUEST_CODE_MY_POSTS = 0x0001;

  private int requestCode;

  public RequireRefreshEvent(int requestCode) {
    this.requestCode = requestCode;
  }

  public int getRequestCode() {
    return requestCode;
  }
}
