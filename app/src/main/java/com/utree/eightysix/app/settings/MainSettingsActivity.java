package com.utree.eightysix.app.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
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

  @InjectView(R.id.cb_silent_mode)
  public CheckBox mCbSilentMode;

  @InjectView(R.id.tv_dev)
  public TextView mTvDev;

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
    Sync sync = U.getSyncClient().getSync();
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

  @OnClick (R.id.tv_dev)
  public void onTvDevClicked() {
    startActivity(new Intent(this, DevModeActivity.class));
  }

  @OnCheckedChanged(R.id.cb_silent_mode)
  public void onCbSilentModeChecked(boolean checked){
    Account.inst().setSilentMode(checked);
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
    Tencent.createInstance(U.getConfig("qq.app_id"), this).joinQQGroup(this, "Q2hi2FH3Mjq27D0jd3s8Vi3zOWl13UHe");
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    Sync sync = U.getSyncClient().getSync();
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

    mCbSilentMode.setChecked(Account.inst().getSilentMode());
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

}