package com.utree.eightysix.app.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;

/**
 * @author simon
 */
@Layout (R.layout.activity_main_settings)
@TopTitle (R.string.settings)
public class MainSettingsActivity extends BaseActivity {

  @OnClick (R.id.rb_logout)
  public void onRbLogoutClicked() {
    AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("确认要注销账号么？")
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

  @OnClick (R.id.tv_check_update)
  public void onTvCheckUpdateClicked() {
    if (U.useFixture()) {
      showProgressBar();
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          hideProgressBar();
        }
      }, 2000);
    }
  }

  @OnClick (R.id.tv_help)
  public void onTvHelpClicked() {

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}