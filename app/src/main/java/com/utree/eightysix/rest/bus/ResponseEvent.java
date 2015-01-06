/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.rest.bus;

import com.utree.eightysix.rest.Response;

/**
 */
public class ResponseEvent<T extends Response> {

  private String mId;

  private T mResponse;

  private Throwable mThrowable;

  public ResponseEvent(String id, T response, Throwable throwable) {
    this.mId = id;
    this.mResponse = response;
    this.mThrowable = throwable;
  }

  public String getId() {
    return mId;
  }

  public T getResponse() {
    return mResponse;
  }

  public Throwable getThrowable() {
    return mThrowable;
  }
}
