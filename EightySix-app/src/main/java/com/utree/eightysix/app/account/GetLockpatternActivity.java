package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.widget.Button;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
@Layout (R.layout.activity_get_lockpattern)
@TopTitle (R.string.get_lock_pattern)
public class GetLockpatternActivity extends BaseActivity {

  @InjectView (R.id.btn_start_find)
  public RoundedButton mBtnStartFind;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onActionLeftOnClicked() {
  }

  /**
   * When LogoutEvent fired, finish myself
   *
   * @param event the logout event
   */
  @Subscribe
  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}