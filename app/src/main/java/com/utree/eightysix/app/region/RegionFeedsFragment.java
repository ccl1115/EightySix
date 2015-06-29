package com.utree.eightysix.app.region;

import android.os.Bundle;
import android.view.View;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.AbsFeedsFragment;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 */
public class RegionFeedsFragment extends AbsFeedsFragment {

  private static final int TYPE_ALL = 0;
  private static final int TYPE_HOT = 1;
  private int mRegionType = Account.inst().getLastRegionType();
  private int mDistance = -1;
  private int mAreaType = 0;
  private int mAreaId = -1;
  private String mAreaName;
  private int mType = TYPE_ALL;

  public int getRegionType() {
    return mRegionType;
  }

  public void setRegionType(int regionType) {
    mRegionType = regionType;
  }

  public int getDistance() {
    return mDistance;
  }

  public void setDistance(int distance) {
    mDistance = distance;
  }

  public int getAreaType() {
    return mAreaType;
  }

  public void setAreaType(int areaType) {
    mAreaType = areaType;
  }

  public int getAreaId() {
    return mAreaId;
  }

  public void setAreaId(int areaId) {
    mAreaId = areaId;
  }

  public String getAreaName() {
    return mAreaName;
  }

  public void setAreaName(String areaName) {
    mAreaName = areaName;
  }

  public int getType() {
    return mType;
  }

  public void setType(int type) {
    mType = type;
  }

  @Override
  protected void updateTitleBar() {

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mIvIcon.setImageResource(R.drawable.ic_action_factories);
    mTvTitle.setText("全部");
  }

  @Override
  protected void request() {
    U.request("feeds_by_region", new OnResponse2<FeedsByRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mLvFeed.loadError();
      }

      @Override
      public void onResponse(FeedsByRegionResponse response) {
        response(response);
      }
    }, FeedsByRegionResponse.class, mPage, mRegionType, mType, mDistance, mAreaType, mAreaName);
  }

  @Override
  protected void cacheOut() {

  }

  @Override
  protected void onPullRefresh() {

  }

  @Override
  protected void onLoadMore(int page) {

  }

  protected void response(FeedsByRegionResponse response) {
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

        mRegionType = response.object.regionType;
        mDistance = response.object.regionRadius;
        mAreaId = response.object.areaId;
        mAreaType = response.object.areaType;
        mAreaName = response.object.cityName;

        Account.inst().setLastRegionType(mRegionType);

        mTvSubInfo.setText(response.object.subInfo);

        U.getBus().post(new RegionResponseEvent(mRegionType, mDistance, mAreaType, mAreaId, mAreaName));
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPage = response.object.posts.page.currPage;
      mHasMore = response.object.posts.lists.size() > 0;

      updateTitleBar();

      FetchNotificationService.setCircleId(0);
    } else {
      cacheOut();
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    mHasMore = false;
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected void responseCache(FeedsByRegionResponse response, int page) {
    if (response != null && response.code == 0 && response.object != null) {
      if (page == 1) {
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

        mRegionType = response.object.regionType;
        mAreaType = response.object.areaType;
        mAreaId = response.object.areaId;
        mAreaName = response.object.cityName;

        updateTitleBar();

        U.getBus().post(new RegionResponseEvent(mRegionType, mDistance, mAreaType, mAreaId, mAreaName));
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
