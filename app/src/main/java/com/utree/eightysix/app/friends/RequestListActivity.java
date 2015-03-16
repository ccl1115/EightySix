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
import com.utree.eightysix.data.FriendRequest;
import com.utree.eightysix.response.FriendRequestResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.List;

/**
 */
@Layout(R.layout.activity_request_list)
@TopTitle(R.string.friend_request)
public class RequestListActivity extends BaseActivity {

  @InjectView(R.id.alv_requests)
  public AdvancedListView mAlvRequests;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));

    mRstvEmpty.setDrawable(R.drawable.scene_1);
    mRstvEmpty.setText("没有收到好友请求");

    showProgressBar(true);

    U.request("user_friend_requests", new OnResponse2<FriendRequestResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FriendRequestResponse response) {
        hideProgressBar();
        if (RESTRequester.responseOk(response)) {
          List<FriendRequest> object = response.object;
          if (object.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mAlvRequests.setAdapter(new RequestListAdapter(object));
          }
        }
      }
    }, FriendRequestResponse.class);
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