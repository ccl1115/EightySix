package com.utree.eightysix.app.region;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.AbsFeedsFragment;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.request.FeedByRegionRequest;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 */
public class RegionFeedsFragment extends AbsFeedsFragment implements RegionSelectFragment.Callback {

  private static final int TYPE_ALL = 0;
  private static final int TYPE_HOT = 1;
  private int mRegionType = Account.inst().getLastRegionType();
  private int mDistance = -1;
  private int mAreaType = 0;
  private int mAreaId = -1;
  private String mAreaName;
  private int mType = TYPE_ALL;

  private RegionSelectFragment mRegionSelectFragment;
  private PopupMenu mPopupMenu;

  public int getRegionType() {
    return mRegionType;
  }

  public void setRegionType(int regionType) {
    mRegionType = regionType;
  }

  @Override
  public void onRegionChanged(int regionType, int distance, int areaType, int areaId) {
    mRegionType = regionType;
    if (distance != -1) {
      mDistance = distance;
    }

    if (areaId != -1) {
      mAreaId = areaId;
    }

    if (areaType != -1) {
      mAreaType = areaType;
    }

    mPage = 1;

    request();
  }

  @Override
  protected void updateTitleBar() {

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mIvIcon.setImageResource(R.drawable.ic_feeds_region);
    mTvTitle.setText("全部");

    mTvSubInfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getBaseActivity().showTopBar(true);
        showLlSubTitle();
        if (mRegionSelectFragment == null) {
          mRegionSelectFragment = new RegionSelectFragment();
          mRegionSelectFragment.mRegionResponseEvent =
              new RegionResponseEvent(mRegionType, mDistance, mAreaType, mAreaId, mAreaName);
          mRegionSelectFragment.setCallback(RegionFeedsFragment.this);
          getChildFragmentManager().beginTransaction()
              .add(R.id.fl, mRegionSelectFragment)
              .commit();
        } else if (mRegionSelectFragment.isHidden()) {
          getChildFragmentManager().beginTransaction()
              .show(mRegionSelectFragment)
              .commit();
        }
      }
    });

    mTvTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mPopupMenu == null) {
          mPopupMenu = new PopupMenu(v.getContext(), v);
          mPopupMenu.inflate(R.menu.feeds_type_menu);
          mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
              switch (item.getItemId()) {
                case R.id.menu_all:
                  if (mType == TYPE_ALL) {
                    return false;
                  } else {
                    mType = TYPE_ALL;
                    mPage = 1;
                    mTvTitle.setText("全部");
                    request();
                    return true;
                  }
                case R.id.menu_hot:
                  if (mType == TYPE_HOT) {
                    return false;
                  } else {
                    mType = TYPE_HOT;
                    mPage = 1;
                    mTvTitle.setText("热门");
                    request();
                    return true;
                  }
              }
              return false;
            }
          });

        }
        if (mRegionSelectFragment != null) {
          mRegionSelectFragment.hideSelf();
        }
        mPopupMenu.show();
      }
    });
  }

  @Override
  protected void request() {
    getBaseActivity().showRefreshIndicator(true);
    showLlSubTitle();

    U.request("feeds_by_region", new OnResponse2<FeedsByRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        if (mPage > 1) {
          mLvFeed.loadError();
        }
        getBaseActivity().hideRefreshIndicator();
      }

      @Override
      public void onResponse(FeedsByRegionResponse response) {
        response(response);
      }
    }, FeedsByRegionResponse.class, mPage, mRegionType, mType, mDistance, mAreaType, mAreaId);
  }

  @Override
  protected void cacheOut() {
    getBaseActivity().cacheOut(new FeedByRegionRequest(mPage, mRegionType, mType, mDistance, mAreaType, mAreaId),
        new OnResponse2<FeedsByRegionResponse>() {
          @Override
          public void onResponseError(Throwable e) {
            if (mPage > 1) {
              mLvFeed.loadError();
            }
            getBaseActivity().hideRefreshIndicator();
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

  private void response(FeedsByRegionResponse response) {
    if (RESTRequester.responseOk(response)) {
      if (mPage == 1) {
        M.getRegisterHelper().unregister(mFeedAdapter);
        mFeedAdapter = new FeedRegionAdapter(response.object, null);
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

  private void responseCache(FeedsByRegionResponse response) {
    if (response != null && response.code == 0 && response.object != null) {
      if (mPage == 1) {
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

      FetchNotificationService.setCircleId(0);
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
