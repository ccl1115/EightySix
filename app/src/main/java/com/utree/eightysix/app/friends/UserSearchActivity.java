/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.os.Bundle;
import android.view.View;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.response.FriendListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
@Layout(R.layout.activity_user_search)
public class UserSearchActivity extends BaseActivity {

  @InjectView(R.id.alv_users)
  public AdvancedListView mAlvUsers;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private UserSearchAdapter mUserSearchAdapter;

  @OnItemClick(R.id.alv_users)
  public void onAlvUsersItemClicked(int position) {
    Friend item = mUserSearchAdapter.getItem(position);
    ProfileFragment.start(this, item.viewId, item.userName);
  }

  @Override
  public void onActionSearchClicked(CharSequence cs) {
    U.request("user_search", new OnResponse2<FriendListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FriendListResponse response) {

        if (RESTRequester.responseOk(response)) {
          if (response.object.isEmpty()) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mUserSearchAdapter = new UserSearchAdapter(response.object);
            mAlvUsers.setAdapter(mUserSearchAdapter);
          }
        }

      }
    }, FriendListResponse.class, cs.toString());
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().enterSearch();

    mRstvEmpty.setText("没有相关的蓝莓用户");
    mRstvEmpty.setDrawable(R.drawable.scene_2);
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