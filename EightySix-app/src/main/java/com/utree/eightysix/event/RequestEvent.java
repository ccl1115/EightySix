package com.utree.eightysix.event;

import com.utree.eightysix.rest.OnResponse;

/**
 * @author simon
 */
public class RequestEvent<T> {

  private Object mRequest;
  private OnResponse<T> mOnResponse;

  private Class<T> mClz;

  public RequestEvent(Object request, OnResponse<T> onResponse, Class<T> clz) {
    mRequest = request;
    mOnResponse = onResponse;
    mClz = clz;
  }

  public Object getRequest() {
    return mRequest;
  }

  public OnResponse<T> getOnResponse() {
    return mOnResponse;
  }


  public Class<T> getClz() {
    return mClz;
  }
}
