package com.utree.eightysix.test;

import android.test.AndroidTestCase;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;

/**
 */
public abstract class BaseAndroidTestCase extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Log.init(getContext());
        Log.activateLogging();
        Log.setDefaultLogLevel(Constants.VERBOSE);
    }
}
