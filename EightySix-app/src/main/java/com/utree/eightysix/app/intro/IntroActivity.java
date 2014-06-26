package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.utils.Env;

/**
 */
public class IntroActivity extends BaseActivity {

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_intro);

    hideTopBar(false);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (Env.firstRun()) {
          startActivity(new Intent(IntroActivity.this, GuideActivity.class));
          finish();
        } else {
          if (Account.inst().isLogin()) {
            //TODO Go to feeds activity or circle list activity
            startActivity(new Intent(IntroActivity.this, FeedActivity.class));
          } else {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
          }
          finish();
        }
      }
    }, U.getConfigInt("activity.intro.delay"));

  }
}
