package com.utree.eightysix;

import android.content.Context;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.statistics.Analyser;
import com.utree.eightysix.statistics.MtaAnalyserImpl;

/**
 * Most helpful methods and singleton instances
 */
public class U {

    private static Analyser sStatistics;

    public static Analyser getAnalyser() {
        if (sStatistics == null) {
            sStatistics = new MtaAnalyserImpl();
        }
        return sStatistics;
    }

    public static Context getContext() {
        return BaseApplication.getContext();
    }
}
