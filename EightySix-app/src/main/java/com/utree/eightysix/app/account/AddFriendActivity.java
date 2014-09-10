package com.utree.eightysix.app.account;

import android.os.Bundle;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.QRCodeScanFragment;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.event.QRCodeScanEvent;

/**
 * @author simon
 */
@TopTitle(R.string.add_new_friend)
@Layout(R.layout.activity_add_friend)
public class AddFriendActivity extends BaseActivity {

  private QRCodeScanFragment mQRCodeScanFragment;

  @OnClick(R.id.ll_scan)
  public void onLlScanClicked() {
    if (mQRCodeScanFragment == null) {
      mQRCodeScanFragment = new QRCodeScanFragment();
    }
    getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
        .add(android.R.id.content, mQRCodeScanFragment)
        .commit();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Subscribe
  public void onQRCodeScanEvent(QRCodeScanEvent event) {
    if (mQRCodeScanFragment != null) {
      if (mQRCodeScanFragment.isVisible()) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
            .remove(mQRCodeScanFragment)
            .commit();
        mQRCodeScanFragment = null;
      }
    }

    showToast("scanned: " + event.getText());
  }

  @Override
  public void onBackPressed() {
    if (mQRCodeScanFragment != null) {
      if (mQRCodeScanFragment.isVisible()) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
            .remove(mQRCodeScanFragment)
            .commit();
        mQRCodeScanFragment = null;
        return;
      }
    }

    super.onBackPressed();
  }
}