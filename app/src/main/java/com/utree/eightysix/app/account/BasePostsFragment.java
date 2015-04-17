/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.response.MyPostsResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
public abstract class BasePostsFragment extends BaseFragment {

  protected static final int PAGE_SIZE = 20;

  @InjectView(R.id.alv_posts)
  public AdvancedListView mAlvPosts;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshLayout;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  protected int mPage = 1;

  private boolean mHasMore;

  private BasePostsAdapter mAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_my_anonymous_posts, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mRefreshLayout.setColorSchemeResources(
        R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed);

    mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        mPage = 1;
        requestPosts();
      }

      @Override
      public void onDrag(int value) {
        getBaseActivity().showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        getBaseActivity().hideRefreshIndicator();
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
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) {
      requestPosts();
    }
  }

  @Override
  protected void onActive() {
    if (isAdded()) {
      requestPosts();
    }
  }

  protected abstract void requestPosts();

  protected void responseForPosts(MyPostsResponse response) {
    mRefreshLayout.setRefreshing(true);
    getBaseActivity().showRefreshIndicator(true);

    if (RESTRequester.responseOk(response)) {
      if (mPage == 1) {
        if (response.object.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mAdapter = new BasePostsAdapter(response.object);
          mAlvPosts.setAdapter(mAdapter);
        }
      } else {
        mAdapter.add(response.object);
      }

      mHasMore = response.object.size() >= PAGE_SIZE;
    }

    mAlvPosts.stopLoadMore();
    mRefreshLayout.setRefreshing(false);
    getBaseActivity().hideRefreshIndicator();
  }
}