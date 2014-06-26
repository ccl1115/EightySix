package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.utils.Env;

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
                if (Account.inst().isLogin()) {
                    //TODO go to feeds activity or circle list activity
                } else {
                    startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                }
                finish();
                Env.setFirstRun(false);
            }
        }, 2000);
    }


  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}