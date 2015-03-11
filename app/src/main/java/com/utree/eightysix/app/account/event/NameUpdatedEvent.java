/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account.event;

/**
 */
public class NameUpdatedEvent {

  private String mName;

  public NameUpdatedEvent(String name) {

    mName = name;
  }

  public String getName() {
    return mName;
  }
}
