package com.utree.eightysix.app.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
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

  @OnClick (R.id.rb_logout)
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

  @OnCheckedChanged(R.id.cb_silent_mode)
  public void onCbSilentModeChecked(boolean checked){
    Account.inst().setSilentMode(checked);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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

    mCbSilentMode.setChecked(Account.inst().getSilentMode());
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}