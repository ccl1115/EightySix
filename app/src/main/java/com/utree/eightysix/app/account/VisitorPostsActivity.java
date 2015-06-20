/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.response.MyPostsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
@Layout(R.layout.activity_visitor_posts)
public class VisitorPostsActivity extends BaseActivity {

  private BasePostsAdapter mAdapter;

  public static void start(Context context, int viewId, String gender) {
    Intent intent = new Intent(context, VisitorPostsActivity.class);
    intent.putExtra("viewId", viewId);
    intent.putExtra("gender", gender);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshLayout;

  @InjectView(R.id.alv_posts)
  public AdvancedListView mAlvPosts;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private int mPage = 1;
  private boolean mHasMore;

  private int mViewId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTopSubTitle("非匿名帖");

    String gender = getIntent().getStringExtra("gender").equals("男") ? "他" : "她";
    setTopTitle(gender + "的帖子");
    mRstvEmpty.setText(gender + "很懒，还没有帖子呃");
    mRstvEmpty.setDrawable(R.drawable.scene_3);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mViewId = getIntent().getIntExtra("viewId", -1);

    mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        showRefreshIndicator(true);
        mPage = 1;
        requestPosts();
      }

      @Override
      public void onDrag(int value) {
        showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        hideRefreshIndicator();
      }
    });

    mAlvPosts.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mHasMore;
      }

      @Override
      public boolean onLoadMoreStart() {
        mPage += 1;
        requestPosts();
        return true;
      }
    });

    showRefreshIndicator(true);
    mRefreshLayout.setRefreshing(true);
    requestPosts();
  }


  private void requestPosts() {
    U.request("user_posts", new OnResponse2<MyPostsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mAlvPosts.stopLoadMore();
        mRefreshLayout.setRefreshing(false);
        hideRefreshIndicator();
      }

      @Override
      public void onResponse(MyPostsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (mPage == 1) {
            if (response.object == null || response.object.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
              mAdapter = new BasePostsAdapter(response.object, 1);
              mAlvPosts.setAdapter(mAdapter);
            }
          } else {
            mAdapter.add(response.object);
          }

          mHasMore = response.object.size() >= 20;
        } else if (response.code == 0x226b8) {
          mRstvEmpty.setVisibility(View.VISIBLE);
          mRstvEmpty.setText("你没有查看帖子的权限");
        }


        mAlvPosts.stopLoadMore();
        mRefreshLayout.setRefreshing(false);
        hideRefreshIndicator();
      }
    }, MyPostsResponse.class, mViewId, 1, mPage, 20);
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