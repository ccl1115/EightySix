/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

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
import com.utree.eightysix.response.FriendListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.PinnedHeaderListView;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
@Layout(R.layout.activity_friend_list)
@TopTitle(R.string.my_friends)
public class FriendListActivity extends BaseActivity {

  @InjectView(R.id.plv_friends)
  public PinnedHeaderListView mAdvFriends;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private FriendListAdapter mFriendListAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));

    mRstvEmpty.setDrawable(R.drawable.scene_4);
    mRstvEmpty.setText("你还没有朋友");

    showProgressBar(true);
    U.request("user_friend_list", new OnResponse2<FriendListResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FriendListResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mFriendListAdapter = new FriendListAdapter(response.object);
            mAdvFriends.setAdapter(mFriendListAdapter);
          }
        }
        hideProgressBar();
      }
    }, FriendListResponse.class);
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