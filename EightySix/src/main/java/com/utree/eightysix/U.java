package com.utree.eightysix;

import android.content.Context;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.disklrucache.DiskLruCache;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.location.BdLocationImpl;
import com.utree.eightysix.location.Location;
import com.utree.eightysix.statistics.Analyser;
import com.utree.eightysix.statistics.MtaAnalyserImpl;
import com.utree.eightysix.storage.Storage;
import com.utree.eightysix.storage.oss.OSSImpl;
import com.utree.eightysix.request.RESTRequester;
import com.utree.eightysix.utils.ViewBinding;
import java.io.IOException;
import java.util.Properties;

/**
 * Most helpful methods and singleton instances
 */
public class U {

    private static final Object lock = new Object();
    private static Analyser sStatistics;
    private static ViewBinding sViewBinding = new ViewBinding();
    private static Location sLocation;
    private static Storage sCloudStorage;
    private static RESTRequester sRESTRequester;
    private static CacheUtils sCacheUtils;

    private static Gson sGson = new GsonBuilder().create();

    private static Properties sConfiguration;

    public static Gson getGson() {
        return sGson;
    }

    public static Analyser getAnalyser() {
        if (sStatistics == null) {
            synchronized (lock) {
                sStatistics = new MtaAnalyserImpl();
            }
        }
        return sStatistics;
    }

    public static Location getLocation() {
        if (sLocation == null) {
            synchronized (lock) {
                sLocation = new BdLocationImpl();
            }
        }
        return sLocation;
    }

    public static Context getContext() {
        return BaseApplication.getContext();
    }

    public static Storage getCloudStorage() {
        if (sCloudStorage == null) {
            synchronized (lock) {
                sCloudStorage = new OSSImpl();
            }
        }
        return sCloudStorage;
    }

    public static <T> T viewBinding(View view, Class<T> holderClass) {
        return sViewBinding.bind(view, holderClass);
    }

    public static <T> void viewBinding(View view, T target) {
        sViewBinding.bind(view, target);
    }

    public static RESTRequester getRESTRequester() {
        if (sRESTRequester == null) {
            synchronized (lock) {
                sRESTRequester = new RESTRequester(getConfig("api.host"));
            }
        }
        return sRESTRequester;
    }

    private static CacheUtils getCacheUtils() {
        if (sCacheUtils == null) {
            synchronized (lock) {
                sCacheUtils = CacheUtils.inst();
            }
        }
        return sCacheUtils;
    }

    public static DiskLruCache getApiCache() {
        return getCacheUtils().getApiCache();
    }

    public static String getConfig(String key) {
        if (sConfiguration == null) {
            sConfiguration = new Properties();
            try {
                sConfiguration.load(U.getContext().getResources().openRawResource(R.raw.configuration));
            } catch (IOException e) {
                U.getAnalyser().reportException(U.getContext(), e);
            }
        }
        return sConfiguration.getProperty(key);
    }

    public static int getConfigInt(String key) {
        try {
            return Integer.parseInt(getConfig(key));
        } catch (NumberFormatException e) {
            U.getAnalyser().reportException(U.getContext(), e);
            return 0;
        }
    }

    public static boolean getConfigBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }
}
