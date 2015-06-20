/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.ladder;

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
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.data.RankedUser;
import com.utree.eightysix.response.BaseLadderResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;

/**
 */
public abstract class BaseLadderFragment extends BaseFragment {

  @InjectView(R.id.alv_ladder)
  public AdvancedListView mAlvLadder;

  private boolean mHasMore;
  private int mPage = 1;
  private BaseLadderAdapter mAdapter;

  @OnItemClick(R.id.alv_ladder)
  public void onAlvLadderItemClicked(int position) {
    Object user = mAdapter.getItem(position);

    if (user instanceof RankedUser) {
      RankedUser user1 = (RankedUser) user;
      ProfileFragment.start(getActivity(), user1.viewId, user1.userName);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_base_ladder, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mAlvLadder.setLoadMoreCallback(new LoadMoreCallback() {
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
        mPage++;
        request();
        return true;
      }
    });

    getBaseActivity().showProgressBar();
    request();
  }

  private void request() {
    U.request(getApi(), new OnResponse2<BaseLadderResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        getBaseActivity().hideProgressBar();
        mAlvLadder.stopLoadMore();
      }

      @Override
      public void onResponse(BaseLadderResponse response) {
        if (RESTRequester.responseOk(response)) {
          mHasMore = response.object != null && response.object.size() > 0;

          if (mPage == 1) {
            mAdapter = new BaseLadderAdapter(response, getApi());
            mAlvLadder.setAdapter(mAdapter);
          } else {
            mAdapter.add(response.object);
          }
        }
        getBaseActivity().hideProgressBar();
        mAlvLadder.stopLoadMore();
      }
    }, BaseLadderResponse.class, mPage);
  }

  protected abstract String getApi();

}