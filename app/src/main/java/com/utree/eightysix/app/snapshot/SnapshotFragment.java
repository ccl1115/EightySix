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
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;

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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);
  }
}
