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
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.app.msg.FetchNotificationService;
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
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.TopBar;

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

  protected int mRegionType = -1;

  protected boolean mPostPraiseRequesting;

  public void setRegionType(int regionType) {
    mRegionType = regionType;
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

    if (isActive()) requestFeeds(mRegionType, 1);
  }

  @Override
  protected void onActive() {
    if (mLvFeed != null) mLvFeed.setAdapter(null);

    if (isAdded()) {
      requestFeeds(mRegionType, 1);
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
          requestFeeds(mRegionType, mPageInfo.currPage + 1);
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
            requestFeeds(mRegionType, 1);
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


          if (post.isHot == 0 && post.isRepost == 0 && Env.firstRun("overlay_tip_source")) {
            mFeedAdapter.showTipOverlaySource(firstItem);
          } else if (Env.firstRun("overlay_tip_praise")) {
            mFeedAdapter.showTipOverlayPraise(firstItem);
          } else if (Env.firstRun("overlay_tip_share")) {
            mFeedAdapter.showTipOverlayShare(firstItem);
          } else if (post.isRepost == 1 && Env.firstRun("overlay_tip_repost")) {
            mFeedAdapter.showTipOverlayRepost(firstItem);
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
        requestFeeds(mRegionType, 1);
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

        getBaseActivity().setTopSubTitle(response.object.subInfo);
        getBaseActivity().setTopSubTitle(response.object.subInfo);

        switch (mRegionType) {
          case 0:
            getBaseActivity().setTopTitle(mCircle.shortName);
            getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_ONE);
            break;
          case 1:
            getBaseActivity().setTopTitle("1公里内");
            getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);
            break;
          case 2:
            getBaseActivity().setTopTitle("5公里内");
            getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);
            break;
          case 3:
            getBaseActivity().setTopTitle("同城的工厂");
            getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);
            break;
        }

        U.getBus().post(new RegionResponseEvent(mRegionType, mCircle));
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPageInfo = response.object.posts.page;

      ((HomeActivity) getBaseActivity()).mSend.setImageResource(response.object.lock != 1 || response.object.current == 1 ?
          R.drawable.ic_post_pen : R.drawable.ic_post_pen_disabled);


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

      }

      FetchNotificationService.setCircleId(mCircle.id);
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

        getBaseActivity().setTopSubTitle(response.object.subInfo);

        switch (mRegionType) {
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

        U.getBus().post(new RegionResponseEvent(mRegionType, mCircle));

      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPageInfo = response.object.posts.page;

      mRegionType = response.object.regionType;

      ((HomeActivity) getBaseActivity()).mSend.setImageResource(response.object.lock != 1 || response.object.current == 1 ?
          R.drawable.ic_post_pen : R.drawable.ic_post_pen_disabled);

      FetchNotificationService.setCircleId(mCircle.id);
    } else {
      if (mFeedAdapter != null && mFeedAdapter.getCount() == 0) {
        mRstvEmpty.setVisibility(View.VISIBLE);
      }
      if (mCircle != null) {
        getBaseActivity().setTopTitle(mCircle.shortName);
      }
      getBaseActivity().setTopSubTitle("");

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


}
