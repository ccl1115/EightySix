/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.rest.bus;

import com.squareup.otto.Bus;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Event driven request util, this is based on RESTRequest.
 * <p/>
 * We start a request by send a {@link com.utree.eightysix.rest.bus.RequestEvent} to the bus.
 * And receive its correspond response by subscribing the ResponseEvent event.
 * The subscribe method may receive many response at same time,
 * use the generated id to identity which response is for your request.
 * <p/>
 * <p/>
 * <b>note this util only works on main thread, it's not designed for multi-thread invoking.</b>
 */
public class RequestBus {

  private static final int MAX = 10;

  private Bus mBus = new Bus("request");

  private List<RequestEvent> mRequesting = new ArrayList<RequestEvent>();

  private Queue<RequestEvent> mQueue = new PriorityQueue<RequestEvent>();

  /**
   * Make a request using the request event
   *
   * @param event the event to fire a request
   * @param <RES> the response type
   */
  public <RES extends Response> void request(final RequestEvent<RES> event) {
    if (has(event)) {
      return;
    }

    if (full()) {
      mQueue.offer(event);
    } else {
      mRequesting.add(event);
      U.getRESTRequester().request(event.getRequestData(), new HandlerWrapper<RES>(event.getRequestData(), new OnResponse2<RES>() {
        @Override
        public void onResponseError(Throwable e) {
          mBus.post(new ResponseEvent<RES>(event.getId(), null, e));

          if (mRequesting.remove(event)) {
            if (!full() && !empty()) {
              mRequesting.add(mQueue.poll());
            }
          }
        }

        @Override
        public void onResponse(RES response) {
          mBus.post(new ResponseEvent<RES>(event.getId(), response, null));

          if (mRequesting.remove(event)) {
            if (!full() && !empty()) {
              mRequesting.add(mQueue.poll());
            }
          }
        }
      }, event.getRequestData().getResClz()));
    }
  }

  /**
   * Make a request using a request data
   *
   * @param data     the request data contain all information for making a http request
   * @param resClass the response class instance
   * @param <RES>    the response type
   * @return the unique generated id for this request
   */
  public <RES extends Response> String request(RequestData<RES> data, Class<RES> resClass) {
    return request(data, resClass, 100);
  }

  /**
   * Make a request using an annotated POJO.
   *
   * @param object the POJO
   * @param resClz the response class instance
   * @param <RES>  the response type
   * @return the unique generated id for this request
   */
  public <RES extends Response> String request(Object object, Class<RES> resClz) {
    return request(object, resClz, 100);
  }

  /**
   * Make a request using a request data
   *
   * @param data     the request data contain all information for making a http request
   * @param resClass the response class instance
   * @param priority the waiting queue's priority
   * @param <RES>    the response type
   * @return the unique generated id for this request
   */
  public <RES extends Response> String request(RequestData<RES> data, Class<RES> resClass, int priority) {
    String id = String.valueOf(data.hashCode());
    data.setResClz(resClass);
    request(new RequestEvent<RES>(id, data, priority));
    return id;
  }

  /**
   * Make a request with a priority
   *
   * @param object   the POJO
   * @param resClz   the response class instance
   * @param priority the waiting queue's priority
   * @param <RES>    the response type
   * @return the unique generated id for this request
   */
  public <RES extends Response> String request(Object object, Class<RES> resClz, int priority) {
    RequestData<RES> data = new RequestData<RES>(object);
    String id = String.valueOf(data.hashCode());
    data.setResClz(resClz);
    request(new RequestEvent<RES>(id, data, priority));
    return id;
  }

  /**
   * Register your object to receive subscribed event.
   *
   * @param object the object to register
   */
  public void register(Object object) {
    M.getRegisterHelper().register(mBus, object);
  }

  /**
   * Unregister your object
   *
   * @param object the object to unregister
   */
  public void unregister(Object object) {
    M.getRegisterHelper().unregister(mBus, object);
  }


  /**
   * @return true if requesting list is full
   */
  private boolean full() {
    return mRequesting.size() >= MAX;
  }

  /**
   * @return true if queue is empty
   */
  private boolean empty() {
    return mQueue.isEmpty();
  }

  /**
   * @return true if the request is already in the requesting list or the waiting queue
   */
  private boolean has(RequestEvent event) {
    for (RequestEvent e : mQueue) {
      if (e.getId().equals(event.getId())) {
        return true;
      }
    }

    for (RequestEvent e : mRequesting) {
      if (e.getId().equals(event.getId())) {
        return true;
      }
    }

    return false;
  }
}
