/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.rest;

import com.squareup.otto.Bus;
import com.utree.eightysix.event.RequestEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Event driven request util
 */
public class RequestBus {

  private static final int MAX = 10;

  private Bus mBus = new Bus("request");

  private List<RequestEvent> mRequesting = new ArrayList<RequestEvent>();

  private Queue<RequestEvent> mQueue = new PriorityQueue<RequestEvent>();

  public void request(RequestEvent event) {

  }

  public String request(RequestData data, Class resClz) {
    return "";
  }

  public String request(Object object, Class resClz) {
    return "";
  }


}
