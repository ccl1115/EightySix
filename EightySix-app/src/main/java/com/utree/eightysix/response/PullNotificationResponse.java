package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class PullNotificationResponse extends Response {

  @SerializedName("object")
  public PullNotification object;

  public static class PullNotification {

    @SerializedName("type")
    public int type;

    @SerializedName("ids")
    public String[] ids;

    @SerializedName("msg")
    public String msg;

    @SerializedName("praise")
    public int praise;
  }
}
