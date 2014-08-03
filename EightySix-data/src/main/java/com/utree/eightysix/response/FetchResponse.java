package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.FetchNotification;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class FetchResponse extends Response {

  @SerializedName("fetchNoticeView")
  public FetchNotification fetchNotification;
}
