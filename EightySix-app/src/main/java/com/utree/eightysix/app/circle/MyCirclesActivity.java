package com.utree.eightysix.app.circle;

import android.app.Activity;
import android.os.Bundle;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.data.Circle;

/**
 * @author simon
 */
@TopTitle(R.string.my_circles)
public class MyCirclesActivity extends BaseCirclesActivity {

  @OnItemClick(R.id.lv_circles)
  public void onLvCirclesItemClicked(int position) {
    Circle circle = mCircleListAdapter.getItem(position);
    if (circle != null) {
      circle.selected = true;
      FeedActivity.start(this, circle);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}