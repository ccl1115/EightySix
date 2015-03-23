/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
@Layout(R.layout.activity_feeds_search)
public class FeedsSearchActivity extends BaseActivity {

  @InjectView(R.id.alv_feeds)
  public AdvancedListView mAlvFeeds;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private FeedsSearchAdapter mFeedsSearchAdapter;

  private int mCurrent;

  private Paginate.Page mPageInfo;

  private String mSearchContent;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mRstvEmpty.setDrawable(R.drawable.scene_5);
    mRstvEmpty.setText("没有搜索结果");

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().enterSearch();

    getTopBar().getSpinner().setVisibility(View.VISIBLE);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item,
        new String[]{"全部", "在职"});

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    getTopBar().getSpinner().setAdapter(adapter);

    getTopBar().getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
          mCurrent = 0;
        } else if (mCurrent == 1) {
          mCurrent = 1;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    mAlvFeeds.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPageInfo != null && mPageInfo.currPage < mPageInfo.countPage;
      }

      @Override
      public boolean onLoadMoreStart() {
        requestFeeds(mPageInfo.currPage + 1);
        return true;
      }
    });
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

  @Override
  public void onActionSearchClicked(CharSequence cs) {
    mSearchContent = cs.toString();
    requestFeeds(1);
  }

  public void requestFeeds(final int page) {
    showProgressBar(true);
    U.request("feeds_search", new OnResponse2<FeedsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FeedsResponse response) {

        if (RESTRequester.responseOk(response)) {

          if (page == 1) {

            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            } else {
              mFeedsSearchAdapter = new FeedsSearchAdapter(response.object.posts.lists);
              mAlvFeeds.setAdapter(mFeedsSearchAdapter);
              mRstvEmpty.setVisibility(View.GONE);
            }

          } else {
            mFeedsSearchAdapter.add(response.object.posts.lists);
          }
        }
        mPageInfo = response.object.posts.page;

        hideProgressBar();
      }
    }, FeedsResponse.class, mSearchContent, null, mCurrent, page);
  }
}