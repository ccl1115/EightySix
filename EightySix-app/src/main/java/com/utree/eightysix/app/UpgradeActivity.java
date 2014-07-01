package com.utree.eightysix.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
@Layout (R.layout.activity_upgrade)
public class UpgradeActivity extends BaseActivity {

  @InjectView (R.id.tv_version)
  public TextView mTvVersion;

  @InjectView (R.id.tv_info)
  public TextView mTvInfo;

  @InjectView (R.id.rb_cancel)
  public RoundedButton mRbCancel;

  @InjectView (R.id.rb_download_upgrade)
  public RoundedButton mRbDownloadUpgrade;

  public boolean mForce;

  public static void start(Context context, String version, String content, boolean force) {
    Intent intent = new Intent(context, UpgradeActivity.class);
    intent.putExtra("version", version);
    intent.putExtra("content", content);
    intent.putExtra("force", force);
    context.startActivity(intent);
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onBackPressed() {
    if (mForce) {
      showToast(getString(R.string.force_update_tip));
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    mTvVersion.setText(U.gfs(R.string.new_version, getIntent().getStringExtra("version")));
    mTvInfo.setText(getIntent().getStringExtra("content"));

    mForce = getIntent().getBooleanExtra("force", false);

    if (mForce) {
      mRbCancel.setVisibility(View.GONE);
    }
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }
}
