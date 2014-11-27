package com.utree.eightysix.event;

import android.support.annotation.NonNull;
import com.utree.eightysix.rest.RequestData;

/**
 * @author simon
 */
public class RequestEvent implements Comparable<RequestEvent> {

  private String mId;

  private RequestData mRequestData;

  private int mPriority;

  public RequestEvent(String id, RequestData requestData) {
    this.mId = id;
    this.mRequestData = requestData;
  }

  public RequestEvent(String id, RequestData requestData, int priority) {
    this(id, requestData);
    mPriority = priority;
  }

  public RequestData getRequestData() {
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
