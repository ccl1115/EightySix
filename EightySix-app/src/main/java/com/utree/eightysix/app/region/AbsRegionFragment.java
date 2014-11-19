package com.utree.eightysix.app.region;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.feed.FeedAdapter;
import com.utree.eightysix.app.feed.event.UpdatePraiseCountEvent;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.msg.event.NewAllPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Feeds;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.*;

/**
 * @author simon
 */
public abstract class AbsRegionFragment extends BaseFragment {

  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView (R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView (R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  protected FeedRegionAdapter mFeedAdapter;
  protected Circle mCircle;
  protected Paginate.Page mPageInfo;

  private int mRegionType = -1;

  protected boolean mPostPraiseRequesting;
  private String mSubInfo;

  public void setRegionType(int regionType) {
    mRegionType = regionType;
  }

  public FeedRegionAdapter getFeedAdapter() {
    return mFeedAdapter;
  }

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position, View view) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null || !(item instanceof Post)) return;
    PostActivity.start(getActivity(), (Post) item);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) requestFeeds(getRegionType(), 1);
  }

  @Override
  protected void onActive() {
    if (mLvFeed != null) mLvFeed.setAdapter(null);

    if (isAdded()) {
      requestFeeds(getRegionType(), 1);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.inject(this, view);

    mLvFeed.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return (mPageInfo != null) && (mPageInfo.currPage < mPageInfo.countPage);
      }

      @Override
      public boolean onLoadMoreStart() {
        if (mPageInfo != null) {
          onLoadMore(mPageInfo.currPage + 1);
          requestFeeds(getRegionType(), mPageInfo.currPage + 1);
          return true;
        } else {
          return false;
        }
      }
    });

    M.getRegisterHelper().register(mLvFeed);

    mRefresherView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        onPullRefresh();
        if (isAdded()) {
          if (mCircle != null) {
            requestFeeds(getRegionType(), 1);
          } else {
            requestFeeds(-1, 1);
          }
        }
      }

      @Override
      public void onDrag() {
        getBaseActivity().showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        getBaseActivity().hideRefreshIndicator();
      }
    });

    mRefresherView.setColorSchemeResources(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);

    mLvFeed.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
          U.getBus().post(new ListViewScrollStateIdledEvent());

          if (view.getChildCount() <= 2) return;

          final int firstItem = mLvFeed.getFirstVisiblePosition() + 1;

          if (mFeedAdapter.tipsShowing() || mFeedAdapter.getItemViewType(firstItem) != FeedAdapter.TYPE_POST) return;

          Post post = (Post) mFeedAdapter.getItem(firstItem);

          if (post.sourceType == 2 && Env.firstRun("overlay_tip_temp_name")) {
            mFeedAdapter.showTipTempName(firstItem);
          } else if ((post.viewType == 1 || post.viewType == 2 || post.viewType == 5)  && Env.firstRun("overlay_tip_source")) {
            mFeedAdapter.showTipSource(firstItem);
          } else if (Env.firstRun("overlay_tip_praise")) {
            mFeedAdapter.showTipPraise(firstItem);
          } else if (Env.firstRun("overlay_tip_share")) {
            mFeedAdapter.showTipShare(firstItem);
          } else if (post.isRepost == 1 && Env.firstRun("overlay_tip_repost")) {
            mFeedAdapter.showTipRepost(firstItem);
          } else if (post.tags != null && post.tags.size() > 0 &&  Env.firstRun("overlay_tip_tags")) {
            mFeedAdapter.showTipTags(firstItem);
          }
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

      }
    });

    getBaseActivity().showRefreshIndicator(true);
    mRefresherView.setRefreshing(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (mLvFeed != null) {
      M.getRegisterHelper().unregister(mLvFeed);
    }

    if (mFeedAdapter != null) {
      M.getRegisterHelper().unregister(mFeedAdapter);
    }
  }

  public Circle getCircle() {
    return mCircle;
  }

  public int getFriendCount() {
    if (mCircle != null) {
      return mCircle.friendCount;
    } else {
      return 0;
    }
  }

  public int getCircleId() {
    return mCircle == null ? 0 : mCircle.id;
  }

  public void refresh() {
    getBaseActivity().showProgressBar();
    if (mCircle != null) {
      if (isAdded()) {
        requestFeeds(getRegionType(), 1);
      }
    } else {
      if (isAdded()) {
        requestFeeds(-1, 1);
      }
    }
  }


  public int getWorkerCount() {
    if (mFeedAdapter != null && mFeedAdapter.getFeeds() != null) {
      return mFeedAdapter.getFeeds().workerCount;
    } else {
      return 0;
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      switch (getRegionType()) {
        case 0:
          getTopBar().setTitle(mCircle == null ? "" : mCircle.shortName);
          break;
        case 1:
          getTopBar().setTitle("1公里内");
          break;
        case 2:
          getTopBar().setTitle("5公里内");
          break;
        case 3:
          getTopBar().setTitle("同城");
          break;
      }
      getTopBar().setSubTitle(mSubInfo == null ? "" : mSubInfo);
    }
  }

  protected abstract void requestFeeds(final int regionType, final int page);

  protected void responseForRequest(FeedsByRegionResponse response, int regionType, int page) {
    if (RESTRequester.responseOk(response)) {
      if (page == 1) {
        mCircle = response.object.circle;

        U.getBus().post(new CurrentCircleResponseEvent(mCircle));

        M.getRegisterHelper().unregister(mFeedAdapter);
        mFeedAdapter = new FeedRegionAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
        }

        mRegionType = response.object.regionType;

        Account.inst().setLastRegionType(getRegionType());

        switch (getRegionType()) {
          case 0:
            getTopBar().setTitle(mCircle.shortName);
            break;
          case 1:
            getTopBar().setTitle("1公里内");
            break;
          case 2:
            getTopBar().setTitle("5公里内");
            break;
          case 3:
            getTopBar().setTitle("同城");
            break;
        }

        U.getBus().post(new RegionResponseEvent(getRegionType(), mCircle));
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPageInfo = response.object.posts.page;
      mSubInfo = response.object.subInfo;
      getTopBar().setSubTitle(mSubInfo);

      if (response.object.fetch != null) {
        if (response.object.fetch.newComment != null) {
          Account.inst().setNewCommentCount(response.object.fetch.newComment.unread);
        }

        if (response.object.fetch.myPostComment != null) {
          Account.inst().setNewCommentCount(response.object.fetch.myPostComment.unread);
        }

        if (response.object.fetch.newPraise != null) {
          Account.inst().setHasNewPraise(response.object.fetch.newPraise.praise == 1);
          U.getBus().post(new UpdatePraiseCountEvent(response.object.fetch.newPraise.praiseCount,
              response.object.fetch.newPraise.percent));
        }

        if (getRegionType() == 0 && mCircle != null) {
          U.getBus().post(new NewAllPostCountEvent(mCircle.id, response.object.fetch.newPostAllCount));
          U.getBus().post(new NewHotPostCountEvent(mCircle.id, response.object.fetch.newPostHotCount));
        } else {
          U.getBus().post(new NewAllPostCountEvent(0, 0));
          U.getBus().post(new NewHotPostCountEvent(0, 0));
        }
      } else {
        U.getBus().post(new NewAllPostCountEvent(0, 0));
        U.getBus().post(new NewHotPostCountEvent(0, 0));
      }

      if (getRegionType() == 0) {
        FetchNotificationService.setCircleId(mCircle == null ? 0 : mCircle.id);
      } else {
        FetchNotificationService.setCircleId(0);
      }
    } else {
      cacheOutFeeds(regionType, page);
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected abstract void cacheOutFeeds(final int regionType, final int page);

  protected void responseForCache(FeedsByRegionResponse response, int regionType, int page) {
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
        mFeedAdapter = new FeedRegionAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        mRegionType = response.object.regionType;


        switch (getRegionType()) {
          case 0:
            getBaseActivity().setTopTitle(mCircle.shortName);
            break;
          case 1:
            getBaseActivity().setTopTitle("1公里内");
            break;
          case 2:
            getBaseActivity().setTopTitle("5公里内");
            break;
          case 3:
            getBaseActivity().setTopTitle("同城");
            break;
        }

        U.getBus().post(new RegionResponseEvent(getRegionType(), mCircle));

      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPageInfo = response.object.posts.page;
      mRegionType = response.object.regionType;
      getBaseActivity().setTopSubTitle(response.object.subInfo);

      FetchNotificationService.setCircleId(mCircle == null ? 0 : mCircle.id);
    } else {
      if (mFeedAdapter != null && mFeedAdapter.getCount() == 0) {
        mRstvEmpty.setVisibility(View.VISIBLE);
      }
      if (mCircle != null) {
        getBaseActivity().setTopTitle(mCircle.shortName);
      }

      mPageInfo = null;
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected abstract void onPullRefresh();

  protected abstract void onLoadMore(int page);

  boolean canPublish() {
    if (mFeedAdapter != null) {
      Feeds feeds = mFeedAdapter.getFeeds();
      if (feeds != null) return feeds.current == 1 || feeds.lock == 0;
    }
    return false;
  }


  public int getRegionType() {
    return mRegionType;
  }
}
