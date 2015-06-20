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

  private RequestListAdapter mRequestListAdapter;

  @OnItemClick(R.id.alv_requests)
  public void onAlvRequestsClicked(int position) {
    RequestDetailActivity.start(this, (FriendRequest) mAlvRequests.getAdapter().getItem(position));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    NotifyUtils.clearAddedFriendNames();

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    Account.inst().setFriendRequestCount(0);

    mRstvEmpty.setDrawable(R.drawable.scene_1);
    mRstvEmpty.setText("还没有朋友请求");
    mRstvEmpty.setSubText("快去加朋友，与大家互动吧");

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
          if (response.object == null || object.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mRequestListAdapter = new RequestListAdapter(object);
            mAlvRequests.setAdapter(mRequestListAdapter);
          }
        }
      }
    }, FriendRequestResponse.class);
  }


  @Subscribe
  public void onFriendRequestEvent(FriendRequest request) {
    if (mRequestListAdapter != null) {
      mRequestListAdapter.update(request);
    }
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