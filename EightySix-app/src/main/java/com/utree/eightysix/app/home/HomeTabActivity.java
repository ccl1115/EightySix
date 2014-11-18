package com.utree.eightysix.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

/**
 */
@Layout(R.layout.activity_home_tab)
public class HomeTabActivity extends BaseActivity {

  @InjectView(R.id.fl_feed)
  public FrameLayout mFlFeed;

  @InjectView(R.id.fl_explore)
  public FrameLayout mFlExplorer;

  @InjectView(R.id.fl_nearby)
  public FrameLayout mFlNearBy;

  @InjectView(R.id.fl_more)
  public FrameLayout mFlMore;

  @InjectView(R.id.iv_send)
  public ImageView mIvSend;

  @OnClick({ R.id.fl_feed, R.id.fl_explore, R.id.fl_nearby, R.id.fl_more })
  public void onTabItemClicked(View v) {
    clearTabSelection();
    v.setSelected(true);
  }

  public static void start(Context context) {
    context.startActivity(getIntent(context));
  }

  public static Intent getIntent(Context context) {
    Intent intent = new Intent(context, HomeTabActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    return intent;
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mFlFeed.setSelected(true);
  }

  private void clearTabSelection() {
    mFlFeed.setSelected(false);
    mFlExplorer.setSelected(false);
    mFlNearBy.setSelected(false);
    mFlMore.setSelected(false);
  }
}
