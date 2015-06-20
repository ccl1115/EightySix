/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.snapshot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import de.akquinet.android.androlog.Log;

/**
 */
public class SnapshotFragment extends BaseFragment {

  @InjectView(R.id.tv_empty_text)
  public RandomSceneTextView mEmptyView;

  @InjectView(R.id.lv_feed)
  public AdvancedListView mAlvFeed;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshView;

  public FeedAdapter mFeedAdapter;

  public Paginate.Page mPageInfo;


  @OnItemClick(R.id.lv_feed)
  public void onAlvFeedItemClicked(int position) {
    Post post = (Post) mFeedAdapter.getItem(position);

    if (post != null) {
      PostActivity.start(getActivity(), post);
    }
  }

  private int mFactoryId;
  private int mSnapshot;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    Log.d("SnapshotFragment", "onViewCreated" + this.toString());
    ButterKnife.inject(this, view);

    mRefreshView.setColorSchemeResources(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);

    mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        requestSnapshotFeed(1);
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

    mAlvFeed.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPageInfo != null && (mPageInfo.currPage < mPageInfo.countPage);
      }

      @Override
      public boolean onLoadMoreStart() {
        requestSnapshotFeed(mPageInfo.currPage + 1);
        return true;
      }
    });

  }

  @Override
  protected void onActive() {
    super.onActive();

    if (isActive()) {
      mRefreshView.setRefreshing(true);
      getBaseActivity().showRefreshIndicator(true);
      requestSnapshotFeed(1);
    }
  }

  public void setFactoryId(int factoryId) {
    mFactoryId = factoryId;
  }

  public void setSnapshot(int snapshot) {
    mSnapshot = snapshot;
  }

  private void requestSnapshotFeed(final int page) {
    U.request("feed_snapshot", new OnResponse2<FeedsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mAlvFeed.stopLoadMore();
        mRefreshView.setRefreshing(false);
        getBaseActivity().hideRefreshIndicator();
        getBaseActivity().hideProgressBar();
      }

      @Override
      public void onResponse(FeedsResponse response) {

        if (RESTRequester.responseOk(response)) {

          if (page == 1) {
            mFeedAdapter = new FeedAdapter(response.object.posts.lists);
            mAlvFeed.setAdapter(mFeedAdapter);

            getBaseActivity().setTopTitle(response.object.circle.shortName);
            getBaseActivity().setTopSubTitle(response.object.circle.info);
          } else {
            mFeedAdapter.add(response.object.posts.lists);
          }

          mPageInfo = response.object.posts.page;
        }

        mAlvFeed.stopLoadMore();
        mRefreshView.setRefreshing(false);
        getBaseActivity().hideRefreshIndicator();
        getBaseActivity().hideProgressBar();
      }
    }, FeedsResponse.class, mFactoryId, mSnapshot, page);
  }
}
