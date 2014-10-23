package com.utree.eightysix.app.topic;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

/**
 */
@Layout (R.layout.activity_topic)
public class TopicActivity extends BaseActivity {

  @InjectView (R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView(R.id.tt_tab)
  public ViewPager mTtTab;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}