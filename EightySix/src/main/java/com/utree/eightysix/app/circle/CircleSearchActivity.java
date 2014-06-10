package com.utree.eightysix.app.circle;

import android.os.Bundle;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

/**
 */
@Layout (R.layout.activity_circle_search)
public class CircleSearchActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getTopBar().enterSearch();
    }
}