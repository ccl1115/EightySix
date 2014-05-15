package com.utree.eightysix.app;

import android.app.Activity;
import com.utree.eightysix.U;

/**
 */
public class BaseActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        U.getAnalyser().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        U.getAnalyser().onPause(this);
    }
}
