package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.region.FeedRegionAdapter;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.FollowCircle;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.request.FeedByRegionRequest;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 */
public class FollowFeedsFragment extends AbsFeedsFragment implements FollowCirclesFragment.Callback {

  private static final int TYPE_ALL = 0;
  private static final int TYPE_HOT = 1;

  private static final int CIRCLE_TYPE_CURRENT = 0;
  private static final int CIRCLE_TYPE_FOLLOW = 1;

  private int mType = TYPE_ALL;

  private int mCircleType = CIRCLE_TYPE_CURRENT;

  private FollowCirclesFragment mFollowCirclesFragment;

  private FollowCircle mFollowCircle;
  private PopupMenu mPopupMenu;

  @Override
  public void onFollowCircleClicked(FollowCircle circle) {
    mCircleType = CIRCLE_TYPE_FOLLOW;
    mPage = 1;
    mFollowCircle = circle;
    getTopBar().setTitleTabText(1, "关注");
    request();
  }

  @Override
  public void onCurrentCircleClicked(Circle circle) {
    mCircleType = CIRCLE_TYPE_CURRENT;
    mPage = 1;
    getTopBar().setTitleTabText(1, "在职");
    request();
  }

  @Override
  protected void updateTitleBar() {

  }

  @Override
  protected void request() {
    getBaseActivity().showRefreshIndicator(true);
    showLlSubTitle();

    if (mCircleType == CIRCLE_TYPE_CURRENT) {
      requestCurrent();
    } else if (mCircleType == CIRCLE_TYPE_FOLLOW) {
      requestFollow();
    }
  }

  private void requestFollow() {
    String path;
    if (mType == TYPE_HOT) {
      path = "feed_list_hot";
    } else if (mType == TYPE_ALL) {
      path = "feed_list";
    } else {
      return;
    }

    U.request(path, new OnResponse2<FeedsByRegionResponse>() {
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
    }, FeedsByRegionResponse.class, mFollowCircle.factoryId, mPage);
  }

  private void requestCurrent() {
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

    mIvIcon.setImageResource(R.drawable.ic_feeds_follow);

    mTvTitle.setText("全部");

    mTvSubInfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getBaseActivity().showTopBar(true);
        showLlSubTitle();
        if (mFollowCirclesFragment == null) {
          mFollowCirclesFragment = new FollowCirclesFragment();
          mFollowCirclesFragment.setCallback(FollowFeedsFragment.this);
          getChildFragmentManager().beginTransaction()
              .add(R.id.fl, mFollowCirclesFragment)
              .commit();
        } else if (mFollowCirclesFragment.isHidden()) {
          getChildFragmentManager().beginTransaction()
              .show(mFollowCirclesFragment)
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
        if (mFollowCirclesFragment != null) {
          mFollowCirclesFragment.hideSelf();
        }
        mPopupMenu.show();
      }
    });
  }


  private void response(FeedsByRegionResponse response) {
    if (RESTRequester.responseOk(response)) {
      if (mPage == 1) {
        mCircle = response.object.circle;

        if (mCircleType == CIRCLE_TYPE_CURRENT) {
          U.getBus().post(new CurrentCircleResponseEvent(mCircle));
        }

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

        mTvSubInfo.setText(response.object.circle.shortName + " | " + response.object.subInfo);
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

        if (mCircleType == CIRCLE_TYPE_CURRENT) {
          U.getBus().post(new CurrentCircleResponseEvent(mCircle));
        }

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

        mTvSubInfo.setText(response.object.circle.shortName + " | " + response.object.subInfo);
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
