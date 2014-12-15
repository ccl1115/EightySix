package com.utree.eightysix.test;

import android.test.InstrumentationTestCase;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;

/**
 */
public abstract class BaseInstrumentationTestCase extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Log.init();
        Log.activateLogging();
        Log.setDefaultLogLevel(Constants.VERBOSE);
    }
}
