package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ForgetPwdActivity;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.app.account.RegisterActivity;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
@Layout (R.layout.activity_intro)
public class IntroActivity extends BaseActivity {

  @InjectView (R.id.rb_join)
  public RoundedButton mRbJoin;

  @InjectView (R.id.tv_login)
  public TextView mTvLogin;

  @InjectView (R.id.tv_forget_pwd)
  public TextView mTvForgetPwd;

  public boolean mCanGoBack;

  @OnClick (R.id.tv_login)
  public void onTvLoginClicked() {
    startActivity(new Intent(this, LoginActivity.class));
  }

  @OnClick (R.id.rb_join)
  public void onRbJoinClicked() {
    startActivity(new Intent(this, RegisterActivity.class));
  }

  @OnClick (R.id.tv_forget_pwd)
  public void onTvForgetPwdClicked() {
    startActivity(new Intent(this, ForgetPwdActivity.class));
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
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

    WindowManager.LayoutParams attributes = getWindow().getAttributes();
    attributes.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
    getWindow().setAttributes(attributes);

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

  @Override
  protected boolean shouldCheckUpgrade() {
    return false;
  }

  @Override
  public void onBackPressed() {
    if (mCanGoBack) {
      super.onBackPressed();
    }

  }

  private void showLogin() {
    mTvForgetPwd.setVisibility(View.VISIBLE);
    mTvLogin.setVisibility(View.VISIBLE);
    mRbJoin.setVisibility(View.VISIBLE);

    ObjectAnimator animator = ObjectAnimator.ofFloat(mTvForgetPwd, "translationY", U.dp2px(200), 0);
    animator.setDuration(1000);
    animator.start();

    animator = ObjectAnimator.ofFloat(mTvLogin, "translationY", U.dp2px(200), 0);
    animator.setDuration(1000);
    animator.start();

    animator = ObjectAnimator.ofFloat(mRbJoin, "translationY", U.dp2px(400), 0);
    animator.setDuration(900);
    animator.start();

    mCanGoBack = true;
  }
}
