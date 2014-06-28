package com.utree.eightysix.app.circle;

import android.app.Activity;
import android.os.Bundle;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Circles;
import com.utree.eightysix.request.MyCirclesRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;

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

    if (!U.useFixture()) {
      requestMyCircles();
      showProgressBar();
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestMyCircles() {
    request(new MyCirclesRequest("", 1), new OnResponse<CirclesResponse>() {
      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0) {
          mCircleListAdapter = new CircleListAdapter(response.object.lists);
          mLvCircles.setAdapter(mCircleListAdapter);
        }
        hideProgressBar();
      }
    }, CirclesResponse.class);
  }
}