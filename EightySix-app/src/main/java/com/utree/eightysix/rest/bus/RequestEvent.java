package com.utree.eightysix.rest.bus;

import android.support.annotation.NonNull;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class RequestEvent<RES extends Response> implements Comparable<RequestEvent> {

  private String mId;

  private RequestData<RES> mRequestData;

  private int mPriority = 100;

  public RequestEvent(String id, RequestData<RES> requestData) {
    this.mId = id;
    this.mRequestData = requestData;
  }

  public RequestEvent(String id, RequestData<RES> requestData, int priority) {
    this(id, requestData);
    mPriority = priority;
  }

  public RequestData<RES> getRequestData() {
    return mRequestData;
  }

  public String getId() {
    return mId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RequestEvent that = (RequestEvent) o;

    if (mId != null ? !mId.equals(that.mId) : that.mId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return mId != null ? mId.hashCode() : 0;
  }

  @Override
  public int compareTo(@NonNull RequestEvent another) {
    if (mPriority > another.mPriority) {
      return 1;
    } else if (mPriority < another.mPriority) {
      return -1;
    } else {
      return 0;
    }
  }
}
