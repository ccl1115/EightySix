/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account.event;

/**
 */
public class SignatureUpdatedEvent {

  private String mText;

  public SignatureUpdatedEvent(String text) {

    mText = text;
  }

  public String getText() {
    return mText;
  }
}
