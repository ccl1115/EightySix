package com.utree.eightysix.app.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.tencent.tauth.Tencent;
import com.utree.eightysix.*;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.devmode.DevModeActivity;
import com.utree.eightysix.app.publish.FeedbackActivity;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
@Layout (R.layout.activity_main_settings)
@TopTitle (R.string.settings)
public class MainSettingsActivity extends BaseActivity {

  @InjectView (R.id.rb_upgrade_dot)
  public RoundedButton mRbUpgradeDot;

  @InjectView(R.id.tv_dev)
  public TextView mTvDev;

  @InjectView(R.id.tv_version)
  public TextView mTvVersion;

  private MsgSettingsFragment mMsgSettingsFragment;

  @OnClick (R.id.tv_logout)
  public void onRbLogoutClicked() {
    AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("确认要注销帐号么？")
        .setPositiveButton("注销", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Account.inst().logout();
          }
        })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    dialog.show();
  }

  @OnClick (R.id.ll_check_update)
  public void onLlCheckUpdateClicked() {
    U.getSyncClient().requestSync();
  }

  @OnClick (R.id.tv_dev)
  public void onTvDevClicked() {
    startActivity(new Intent(this, DevModeActivity.class));
  }

  @OnClick(R.id.tv_msg_settings)
  public void onLlMsgSettingsClicked() {
    if (mMsgSettingsFragment == null) {
      mMsgSettingsFragment = new MsgSettingsFragment();
    }
    getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom)
        .add(R.id.content, mMsgSettingsFragment)
        .addToBackStack("backStack")
        .commit();
  }

  @OnClick(R.id.tv_help)
  public void onTvHelpClicked() {
    startActivity(new Intent(this, HelpActivity.class));
  }

  @OnClick(R.id.tv_feedback)
  public void onTvFeedbackClicked() {
    FeedbackActivity.start(this);
  }

  @OnClick(R.id.tv_join_qq)
  public void onTvJoinQQClicked() {
    Tencent.createInstance(U.getConfig("qq.app_id"), this).joinQQGroup(this, U.getSyncClient().getSync().qqGroup);
  }

  @Override
  public void onActionLeftClicked() {
    onBackPressed();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    try {
      mTvVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
    } catch (PackageManager.NameNotFoundException ignored) {
    }

  }

  @Subscribe
  public void onSyncEvent(Sync sync) {
    if (sync != null && sync.upgrade != null) {
      int v = 0;
      try {
        v = Integer.parseInt(sync.upgrade.version);
      } catch (NumberFormatException ignored) {
      }
      if (v > C.VERSION) {
        mRbUpgradeDot.setVisibility(View.VISIBLE);
      } else {
        mRbUpgradeDot.setVisibility(View.INVISIBLE);
      }
    }

    if (BuildConfig.DEBUG) {
      mTvDev.setVisibility(View.VISIBLE);
    }

    int version = 0;
    if (sync != null && sync.upgrade != null) {
      try {
        version = Integer.parseInt(sync.upgrade.version);
      } catch (NumberFormatException ignored) {
      }
      if (version > C.VERSION) {
        new UpgradeDialog(this, sync.upgrade).show();
      } else {
        showToast(getString(R.string.newest_version));
      }
    } else {
      showToast(getString(R.string.newest_version));
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }


  @Override
  public void onBackPressed() {
    super.onBackPressed();

    setTopTitle(getString(R.string.settings));
  }
}