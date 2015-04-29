/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.view.View;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.response.MyPostsResponse;
import com.utree.eightysix.rest.OnResponse2;

/**
 */
public class MyRealNamePostsFragment extends BasePostsFragment {

  public static MyRealNamePostsFragment getInstance() {
    return new MyRealNamePostsFragment();
  }


  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mRstvEmpty.setDrawable(R.drawable.scene_2);
    mRstvEmpty.setText("还没有非匿名帖");
    mRstvEmpty.setSubText("快去发帖，与大家互动吧");
  }

  @Override
  protected void requestPosts() {

    U.request("user_posts", new OnResponse2<MyPostsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mAlvPosts.stopLoadMore();
        mRefreshLayout.setRefreshing(false);
        getBaseActivity().hideRefreshIndicator();
      }

      @Override
      public void onResponse(MyPostsResponse response) {
        responseForPosts(response);
      }
    }, MyPostsResponse.class, null, 1, mPage, PAGE_SIZE);
  }
}
