/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.circle;

import android.os.Bundle;
import android.view.View;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.response.FollowCircleListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
@Layout(R.layout.activity_follow_circle_list)
@TopTitle(R.string.follow_circle)
public class FollowCircleListActivity extends BaseActivity {

  @InjectView(R.id.alv_follow_circles)
  public AdvancedListView mAlvFollowCircles;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;
  private FollowCircleListAdapter mAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));

    getTopBar().getAbRight().setText("编辑");
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mAdapter != null) {
          mAdapter.toggleDelete();
        }
      }
    });

    mRstvEmpty.setDrawable(R.drawable.scene_3);
    mRstvEmpty.setText("你还没有关注任何圈子");

    U.request("follow_circle_list", new OnResponse2<FollowCircleListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FollowCircleListResponse response) {
        if (response.object.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
          mAdapter = new FollowCircleListAdapter(response.object);
          mAlvFollowCircles.setAdapter(mAdapter);
        }
      }
    }, FollowCircleListResponse.class);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}