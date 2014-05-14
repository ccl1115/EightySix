package com.utree.eightysix;

import com.utree.eightysix.statistics.MtaImpl;
import com.utree.eightysix.statistics.Statistics;

/**
 * Most helpful methods and singleton instances
 */
public class U {

    private static Statistics sStatistics;

    public static Statistics getStatistics() {
        if (sStatistics == null) {
            sStatistics = new MtaImpl();
        }
        return sStatistics;
    }
}
