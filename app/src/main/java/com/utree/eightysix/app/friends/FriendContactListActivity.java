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
import com.utree.eightysix.response.FriendListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
@Layout(R.layout.activity_friend_contact_list)
public class FriendContactListActivity extends BaseActivity {

  @InjectView(R.id.alv_contacts)
  public AdvancedListView mAlvContacts;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private FriendContactListAdapter mAdapter;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    getTopBar().setTitle("添加手机联系人");

    showProgressBar(true);
    U.request("user_friend_contacts", new OnResponse2<FriendListResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FriendListResponse response) {
        if (response.object.isEmpty()) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
          mAdapter = new FriendContactListAdapter(response.object);
          mAlvContacts.setAdapter(mAdapter);
        }
        hideProgressBar();
      }
    }, FriendListResponse.class);
  }

  @Subscribe
  public void onSentRequestEvent(SendRequestActivity.SentRequestEvent event) {
    mAdapter.setSentRequest(event.getViewId());
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