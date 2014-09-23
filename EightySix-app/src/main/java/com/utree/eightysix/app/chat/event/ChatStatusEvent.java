package com.utree.eightysix.app.chat.event;

/**
 * @author simon
 */
public class ChatStatusEvent {
  public static final int EVENT_LOGIN_SUC = 1;
  public static final int EVENT_LOGIN_PROGRESS = 2;
  public static final int EVENT_LOGIN_ERR = 3;

  public static final int EVENT_RECEIVE_MSG = 11;
  public static final int EVENT_SENT_MSG = 12;

  private int mStatus;

  private Object mObj;

  public ChatStatusEvent(int status, Object obj) {
    mStatus = status;
    mObj = obj;
  }

  public int getStatus() {
    return mStatus;
  }

  public Object getObj() {
    return mObj;
  }
}
