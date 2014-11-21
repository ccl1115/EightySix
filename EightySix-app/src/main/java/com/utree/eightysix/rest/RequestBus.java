/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.rest;

import com.squareup.otto.Bus;
import com.utree.eightysix.event.RequestEvent;

/**
 * Event driven request util
 */
public class RequestBus {

  private Bus mBus = new Bus("request");

  public void request(RequestEvent event) {

  }

  public String request(RequestData data, Class resClz) {
    return "";
  }

  public String request(Object object, Class resClz) {
    return "";
  }

}
