package com.utree.eightysix;

import android.content.Context;
import android.view.View;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.location.BdLocationImpl;
import com.utree.eightysix.location.Location;
import com.utree.eightysix.statistics.Analyser;
import com.utree.eightysix.statistics.MtaAnalyserImpl;
import com.utree.eightysix.storage.Storage;
import com.utree.eightysix.storage.oss.OSSImpl;
import com.utree.eightysix.utils.ViewBinding;

/**
 * Most helpful methods and singleton instances
 */
public class U {

    private static Analyser sStatistics;
    private static ViewBinding sViewBinding;
    private static Location sLocation;
    private static Storage sCloudStorage;

    static {
        sViewBinding = new ViewBinding();
    }

    private static final Object lock = new Object();

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
}
