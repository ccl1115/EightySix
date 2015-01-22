package com.utree.eightysix.app.chat.event;

/**
 * @author simon
 */
public class ChatEvent {
  public static final int EVENT_LOGIN_SUC = 1;
  public static final int EVENT_LOGIN_PROGRESS = 2;
  public static final int EVENT_LOGIN_ERR = 3;

  /**
   * Event when receive a msg from server
   */
  public static final int EVENT_RECEIVE_MSG = 11;


  /**
   * Event when send msg success
   */
  public static final int EVENT_SENT_MSG_SUCCESS = 12;

  public static final int EVENT_SENDING_MSG = 13;


  /**
   * Event when send msg error
   */
  public static final int EVENT_SENT_MSG_ERROR = 14;

  public static final int EVENT_UPDATE_MSG = 15;

  /**
   * Event to remove a message
   */
  public static final int EVENT_MSG_REMOVE = 21;


  /**
   * Event when conversation data update from db
   */
  public static final int EVENT_CONVERSATION_INSERT_OR_UPDATE = 31;

  public static final int EVENT_CONVERSATION_UPDATE = 33;

  /**
   * Event to reload all conversation from db
   */
  public static final int EVENT_CONVERSATIONS_RELOAD = 34;

  /**
   * Event fire when conversation remove from db
   */
  public static final int EVENT_CONVERSATION_REMOVE = 35;

  /**
   * Event fire when db return unread conversation count
   */
  public static final int EVENT_UPDATE_UNREAD_CONVERSATION_COUNT = 36;

  private int mStatus;

  private Object mObj;

  public ChatEvent(int status, Object obj) {
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
