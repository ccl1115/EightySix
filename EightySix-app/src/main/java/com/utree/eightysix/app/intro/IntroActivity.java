package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
@Layout(R.layout.activity_intro)
public class IntroActivity extends BaseActivity {

  @InjectView(R.id.rb_join)
  public RoundedButton mRbJoin;

  @InjectView(R.id.tv_login)
  public TextView mTvLogin;

  @InjectView(R.id.tv_forget_pwd)
  public TextView mTvForgetPwd;

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // start push service in main entry activity
    // note:
    // put this in Application#onCreate() entry, if push service crashed,
    // it will cause app restart infinitely.
    U.getPushHelper().startWork();

    hideTopBar(false);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (Env.firstRun()) {
          startActivity(new Intent(IntroActivity.this, GuideActivity.class));
          finish();
        } else if (Account.inst().isLogin()) {
          startActivity(new Intent(IntroActivity.this, FeedActivity.class));
          finish();
        } else {
          showLogin();
        }
      }
    }, U.getConfigInt("activity.intro.delay"));

  }

  private void showLogin() {
    mTvForgetPwd.setVisibility(View.VISIBLE);
    mTvLogin.setVisibility(View.VISIBLE);
    mRbJoin.setVisibility(View.VISIBLE);
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  public void onBackPressed() {
  }
}
