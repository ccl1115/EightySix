package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.view.View;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.region.FeedRegionAdapter;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.request.FeedByRegionRequest;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

/**
 */
public class FollowFeedsFragment extends AbsFeedsFragment {

  private static final int TYPE_ALL = 0;
  private static final int TYPE_HOT = 1;

  private int mType = TYPE_ALL;

  @Override
  protected void updateTitleBar() {

  }

  @Override
  protected void request() {

    U.request("feeds_by_region", new OnResponse2<FeedsByRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FeedsByRegionResponse response) {
        response(response);
      }
    }, FeedsByRegionResponse.class, mPage, 0, mType, 0, 0, 0);
  }

  @Override
  protected void cacheOut() {
    getBaseActivity().cacheOut(new FeedByRegionRequest(mPage, 0, mType, 0, 0, 0),
        new OnResponse2<FeedsByRegionResponse>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(FeedsByRegionResponse response) {
            responseCache(response);
          }
        }, FeedsByRegionResponse.class);
  }

  @Override
  protected void onPullRefresh() {

  }

  @Override
  protected void onLoadMore(int page) {

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mIvIcon.setImageResource(R.drawable.ic_action_factories);

    mTvTitle.setText("全部");
  }


  private void response(FeedsByRegionResponse response) {
    if (RESTRequester.responseOk(response)) {
      if (mPage == 1) {
        mCircle = response.object.circle;

        U.getBus().post(new CurrentCircleResponseEvent(mCircle));

        M.getRegisterHelper().unregister(mFeedAdapter);
        mFeedAdapter = new FeedRegionAdapter(response.object, response.extra);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        if (response.object.posts.lists.size() == 0) {

          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
        }

        Account.inst().setLastRegionType(response.object.regionType);

        mTvSubInfo.setText(response.object.subInfo);
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPage = response.object.posts.page.currPage;
      mHasMore = response.object.posts.lists.size() > 0;

      updateTitleBar();

      FetchNotificationService.setCircleId(mCircle == null ? 0 : mCircle.id);
    } else {
      cacheOut();
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    mHasMore = false;
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  private void responseCache(FeedsByRegionResponse response) {
    if (response != null && response.code == 0 && response.object != null) {
      if (mPage == 1) {
        mCircle = response.object.circle;

        U.getBus().post(new CurrentCircleResponseEvent(mCircle));

        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.INVISIBLE);
        }

        M.getRegisterHelper().unregister(mFeedAdapter);
        mFeedAdapter = new FeedRegionAdapter(response.object, response.extra);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        updateTitleBar();

      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPage = response.object.posts.page.currPage;
      mHasMore = response.object.posts.lists.size() > 0;

      FetchNotificationService.setCircleId(mCircle == null ? 0 : mCircle.id);
    } else {
      if (mFeedAdapter != null && mFeedAdapter.getCount() == 0) {
        mRstvEmpty.setVisibility(View.VISIBLE);
      }
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    mHasMore = false;
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }
}
