package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.RegisterActivity;
import com.utree.eightysix.utils.EnvUtils;

/**
 */
@Layout(R.layout.activity_guide)
public class GuideActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GuideActivity.this, RegisterActivity.class));
                finish();
                EnvUtils.setFirstRun(false);
            }
        }, 2000);
    }


}