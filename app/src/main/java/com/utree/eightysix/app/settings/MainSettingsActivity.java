package com.utree.eightysix.app.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.*;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.devmode.DevModeActivity;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.widget.RoundedButton;

import java.io.IOException;

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

  @InjectView(R.id.tv_cache_size)
  public TextView mTvCacheSize;

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

  @OnClick(R.id.ll_clear_cache)
  public void onLlClearCacheClicked() {
    showProgressBar(true);
    new ClearCacheWorker().execute();
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

    if (BuildConfig.DEBUG) {
      mTvDev.setVisibility(View.VISIBLE);
    }

    mCbSilentMode.setChecked(Account.inst().getSilentMode());

    new CacheSizeWorker().execute();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  public class CacheSizeWorker extends AsyncTask<Void, Void, Long> {

    @Override
    protected Long doInBackground(Void... voids) {
      return U.getImageCache().size() + U.getApiCache().size() + U.getContactsCache().size();
    }

    @Override
    protected void onPostExecute(Long aLong) {
      String sizeInHuman;

      int mb = 1024 * 1024;
      if (aLong > mb) {

        sizeInHuman = String.valueOf(aLong / mb);
        sizeInHuman += ".";
        sizeInHuman += String.valueOf(aLong % mb).substring(0, 2) + "MB";
      } else {
        sizeInHuman = (aLong / 1024) + "KB";
      }

      mTvCacheSize.setText("目前缓存大小：" + sizeInHuman);
    }
  }

  public class ClearCacheWorker extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
      try {
        U.getApiCache().delete();
        U.getApiCache().flush();
        U.getImageCache().delete();
        U.getImageCache().flush();
        U.getContactsCache().delete();
        U.getContactsCache().flush();
      } catch (IOException ignored) {
        if (BuildConfig.DEBUG) {
          ignored.printStackTrace();
        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      mTvCacheSize.setText("目前缓存大小：0KB");
      showToast(getString(R.string.clear_cache_succeed));
      hideProgressBar();
    }
  }
}