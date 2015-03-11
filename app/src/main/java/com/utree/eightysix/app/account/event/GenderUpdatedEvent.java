/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account.event;

/**
 */
public class GenderUpdatedEvent {

  private String mGender;

  public GenderUpdatedEvent(String gender) {

    mGender = gender;
  }

  public String getGender() {
    return mGender;
  }
}
