package com.utree.eightysix.event;

import com.utree.eightysix.rest.RequestData;

/**
 * @author simon
 */
public class RequestEvent {

  private String mId;

  private RequestData mRequestData;

  private Class mResClz;

  public RequestEvent(String mId, RequestData mRequestData, Class mResClz) {
    this.mId = mId;
    this.mRequestData = mRequestData;
    this.mResClz = mResClz;
  }

  public Class getResClz() {
    return mResClz;
  }

  public RequestData getRequestData() {
    return mRequestData;
  }

  public String getId() {
    return mId;
  }
}
