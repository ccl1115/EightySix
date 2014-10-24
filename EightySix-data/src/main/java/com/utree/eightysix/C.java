package com.utree.eightysix;

/**
 * <b>C</b> is abbreviation for configuration
 * <p/>
 * All system-wide configuration are defined here.
 *
 * 命名规范
 *
 * API_模块_动作_修饰
 *
 * Naming conversion
 *
 * API_MODULE_ACTION_MODIFIER
 */
public class C {

  public static final String API_ACCOUNT_LOGIN = "/login/login.do";
  public static final String API_ACCOUNT_REG = "/login/reg.do";
  public static final String API_ACCOUNT_REG_SMS = "/login/regSms.do";
  public static final String API_ACCOUNT_REG_HOT = "/login/regHot.do";
  public static final String API_FIND_PWD_1 = "/login/step1.do";
  public static final String API_FIND_PWD_2 = "/login/step2.do";
  public static final String API_FIND_PWD_3 = "/login/step3.do";
  public static final String API_LOGOUT = "/login/logout.do";
  public static final String API_VALICODE_FIND_PWD = "/login/valiCode.do";

  public static final String API_USER_REPORT = "/user/report.do";

  public static final String API_FACTORY_MY = "/factory/my.do";
  public static final String API_FACTORY_FIND = "/factory/find.do";
  public static final String API_FACTORY_SEARCH = "/factory/search.do";
  public static final String API_FACTORY_ADD = "/factory/add.do";
  public static final String API_FACTORY_SET = "/factory/set.do";
  public static final String API_FACTORY_SIDE = "/factory/side.do";
  public static final String API_VALICODE_CREATE_FACTORY = "/factory/valiCode.do";


  public static final String API_FEED_ADD = "/feed/add.do";
  public static final String API_FEED_DELETE = "/feed/delete.do";
  public static final String API_FEED_LIST = "/feed/list.do";
  public static final String API_FEED_HOT_LIST = "/feed/listHot.do";
  public static final String API_FEED_FRIENDS_LIST = "/feed/listFriends.do";
  public static final String API_FEED_PRAISE = "/feed/praise.do";
  public static final String API_FEED_PRAISE_CANCEL = "/feed/cancelPraise.do";
  public static final String API_FEED_SUBMIT_ANSWER = "/feed/submitAnswer.do";
  public static final String API_FEED_CHANGE_NAME = "/feed/changeName.do";
  public static final String API_FEED_OPTION_BACK = "/feed/back.do";

  public static final String API_TOPIC_LIST = "/topic/list.do";

  public static final String API_COMMENT_ADD = "/comment/add.do";
  public static final String API_COMMENT_DELETE = "/comment/delete.do";
  public static final String API_COMMENT_LIST = "/comment/list.do";
  public static final String API_COMMENT_PRAISE = "/comment/praise.do";
  public static final String API_COMMENT_PRAISE_CANCEL = "/comment/cancelPraise.do";

  public static final String API_NOTIFICATION = "/notice/notice.do";
  public static final String API_FETCH = "/notice/fetch.do";
  public static final String API_MSG_LIST = "/remind/list.do";
  public static final String API_PRAISE_LIST = "/remind/praiseList.do";

  public static final String API_MY_FRIENDS = "/friend/myFriend.do";
  public static final String API_CONTACT_FRIENDS = "/friend/contactFriends.do";
  public static final String API_SCAN_FRIENDS = "/friend/qrCodeFriends.do";
  public static final String API_UPLOAD_CONTACTS = "/friend/upContacts.do";
  public static final String API_ADD_FRIEND = "/friend/addFriend.do";
  public static final String API_UNREG_CONTACTS = "/friend/unRegContact.do";

  public static final String API_PROMOTION = "/activity.do";

  public static final String API_ACTIVE_ACCEPT = "/active/acceptPrize.do";
  public static final String API_ACTIVE_JOIN = "/active/join.do";

  public static final String API_SYNC = "/system/sync.do";


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

    public static final String NT = "Notification";
    public static final String ACCOUNT = "Account";
  }
}
