package com.utree.eightysix;

/**
 * <b>C</b> is abbreviation for configuration
 * <p/>
 * All system-wide configuration are defined here.
 */
public class C {

  public static final String API_LOGIN = "/login/login.do";
  public static final String API_REG = "/login/reg.do";
  public static final String API_SHOW_INFO = "/login/showInfo.do";
  public static final String API_INFO_BY_FRIEND = "/login/infoByFriend.do";
  public static final String API_FIND_FRIEND_INFO = "/login/infoFriendInfo.do";
  public static final String API_FIND_PWD_1 = "/login/step1.do";
  public static final String API_FIND_PWD_2 = "/login/step2.do";
  public static final String API_FIND_PWD_3 = "/login/step3.do";
  public static final String API_LOGOUT = "/login/logout.do";

  public static final String API_FACTORY_MY = "/factory/my.do";
  public static final String API_FACTORY_FIND = "/factory/find.do";
  public static final String API_FACTORY_SEARCH = "/factory/search.do";
  public static final String API_FACTORY_ADD = "/factory/add.do";

  public static final String API_UPLOAD_CONTACTS = "/friend/upContacts.do";

  public static final String API_FEED_PUBLISH = "/feed/add.do";
  public static final String API_FEED_LIST = "/feed/list.do";
  public static final String API_FEED_PRAISE = "/feed/praise.do";
  public static final String API_FEED_CANCEL_PRAISE = "/feed/cancelPraise.do";

  public static final String API_COMMENT_ADD = "/comment/add.do";
  public static final String API_COMMENT_LIST = "/comment/list.do";

  public static final String API_VALICODE_FIND_PWD = "/login/valiCode.do";

  public static final String API_VALICODE_CREATE_FACTORY = "/factory/valiCode.do";


  public static String IMEI = "";

  /**
   * VersionCode from AndroidManifest.xml
   */
  public static int VERSION = 0;

  /**
   * Tags for logging
   */
  public static class TAG {
    public static final String AH = "AsyncHttp";

    /**
     * Tag for RESTRequest
     */
    public static final String RR = "RESTRequest";
  }
}
