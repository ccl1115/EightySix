/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account.event;

/**
 */
public class HometownUpdatedEvent {

  private String mName;

  public HometownUpdatedEvent(String name) {

    mName = name;
  }

  public String getName() {
    return mName;
  }
}
