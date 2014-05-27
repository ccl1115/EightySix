package com.utree.eightysix;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>C</b> is abbreviation for configuration
 * <p/>
 * All system-wide configuration are defined here.
 */
public class C {

    /**
     * Registered API
     */
    public static final Map<String, String> API = new HashMap<String, String>();

    public static final String API_LOGIN = "api_login";

    static {
        API.put(API_LOGIN, "/login/login.do");
    }

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
