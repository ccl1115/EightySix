package com.utree.eightysix.app.intro;

import android.content.ComponentName;
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
import com.utree.eightysix.M;
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
import de.akquinet.android.androlog.Log;

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
  private boolean mResumed;

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
    // I don't finish when user logout
  }

  @Subscribe
  public void onLogin(Account.LoginEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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

    M.getLocation().requestLocation();

    if (Env.firstRun("install_shortcut")) {
      Log.d("IntroActivity", "install shortcut");
      addShortcut();
      Env.setFirstRun("install_shortcut", false);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (!mResumed) {
      // start push service in main entry activity
      // note:
      // put this in Application#onCreate() entry, if push service crashed,
      // it will cause app restart infinitely.
      U.getPushHelper().startWork();
      mResumed = true;
    }
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


  private void addShortcut() {
    Intent target = new Intent();
    target.addCategory(Intent.CATEGORY_LAUNCHER);
    target.setAction(Intent.ACTION_MAIN);
    target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    ComponentName comp = new ComponentName(this.getPackageName(),
        this.getPackageName() + ".app.intro.IntroActivity");
    target.setComponent(comp);

    Intent shortcut = new Intent(
        "com.android.launcher.action.INSTALL_SHORTCUT");
    shortcut.putExtra("duplicate", false);
    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
        getString(R.string.app_name));
    shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);

    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(
        this, R.drawable.ic_launcher));
    sendBroadcast(shortcut);
  }
}
