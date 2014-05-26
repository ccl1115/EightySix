package com.utree.eightysix;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>C</b> is abbreviation for configuration
 * <p/>
 * All system-wide configuration are defined here.
 */
public class C {

    public static final String HOST = "http://127.0.0.1";

    /**
     * Registered API
     */
    public static final Map<String, String> API = new HashMap<String, String>();

    public static final String API_LOGIN = "api_login";

    static {
        API.put(API_LOGIN, "/login");
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
    }

    public static class CONFIG_KEY {
        public static final String CHANNEL = "app.channel";

        public static final String CACHE_VERSION = "app.cache_version";

        public static final String CACHE_COUNT = "app.cache_count";

        public static final String CACHE_SIZE = "app.cache_size";
    }
}
