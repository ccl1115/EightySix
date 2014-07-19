package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.PullNotification;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class PullNotificationResponse extends Response {

  @SerializedName("object")
  public PullNotification object;

}
