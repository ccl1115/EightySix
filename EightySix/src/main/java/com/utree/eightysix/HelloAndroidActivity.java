package com.utree.eightysix;

import android.os.Bundle;
import com.utree.eightysix.app.BaseActivity;

public class HelloAndroidActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        U.getAnalyser().trackEvent(this, "onCreate", "");
    }

}

