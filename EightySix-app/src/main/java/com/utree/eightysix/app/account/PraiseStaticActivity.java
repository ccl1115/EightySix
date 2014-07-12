package com.utree.eightysix.app.account;

import android.app.Activity;
import android.os.Bundle;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;

/**
 * @author simon
 */
@Layout(R.layout.activity_praise_static)
@TopTitle(R.string.praise_count_static)
public class PraiseStaticActivity extends BaseActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  /**
   * Do nothing because I'm the login activity
   *
   * @param event the logout event
   */
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}