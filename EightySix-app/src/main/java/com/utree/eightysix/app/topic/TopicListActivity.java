package com.utree.eightysix.app.topic;

import android.app.Activity;
import android.os.Bundle;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

/**
 */
@Layout(R.layout.activity_topic_list)
public class TopicListActivity extends BaseActivity {
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