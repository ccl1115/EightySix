/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.os.Bundle;
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
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
@Layout(R.layout.activity_friend_list)
@TopTitle(R.string.my_friends)
public class FriendListActivity extends BaseActivity {

  @InjectView(R.id.aiv_friends)
  public AdvancedListView mAdvFriends;

  private FriendListAdapter mFriendListAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    U.request("friend_list", new OnResponse2<FriendListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FriendListResponse response) {
        if (RESTRequester.responseOk(response)) {
          mFriendListAdapter = new FriendListAdapter(response.object);

          mAdvFriends.setAdapter(mFriendListAdapter);
        }
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