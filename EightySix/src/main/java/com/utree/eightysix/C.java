package com.utree.eightysix;

import java.util.HashMap;
import java.util.Map;

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