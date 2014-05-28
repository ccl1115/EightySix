package com.utree.eightysix.app.account;

import android.os.Bundle;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

/**
 */
@Layout(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTopTitle(getString(R.string.register) + getString(R.string.app_name));
    }
}