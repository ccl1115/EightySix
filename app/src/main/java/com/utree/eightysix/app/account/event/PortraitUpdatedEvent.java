/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account.event;

/**
 */
public class PortraitUpdatedEvent {

  private String mUrl;

  public PortraitUpdatedEvent(String url) {

    mUrl = url;
  }

  public String getUrl() {
    return mUrl;
  }
}
