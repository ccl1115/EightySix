package com.utree.eightysix;

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
}
