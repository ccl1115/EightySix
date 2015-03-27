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
import com.utree.eightysix.app.msg.event.NewFriendsPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.app.region.event.CircleResponseEvent;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.app.snapshot.SnapshotActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Feeds;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 * @author simon
 */
public abstract class AbsRegionFragment extends BaseFragment {

  public static final int MODE_REGION = 1;
  public static final int MODE_FEED = 2;

  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView (R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView (R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  protected FeedRegionAdapter mFeedAdapter;

  protected Circle mCircle;

  protected Paginate.Page mPageInfo;

  private int mCircleId;

  protected int mRegionType = -1;
  protected int mDistance = 0;

  protected int mMode = MODE_REGION;

  protected boolean mPostPraiseRequesting;
  private String mSubInfo;

  public void requestRegion(int regionType) {
    mMode = MODE_REGION;
    mRegionType = regionType;
  }

  public void requestFeeds(int circleId) {
    mMode = MODE_FEED;
    mCircleId = circleId;
  }

  public FeedRegionAdapter getFeedAdapter() {
    return mFeedAdapter;
  }

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null || !(item instanceof Post)) return;
    PostActivity.start(getActivity(), (Post) item);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) {
      if (mMode == MODE_REGION) {
        requestRegionFeeds(mRegionType, mDistance, 1);
      } else if (mMode == MODE_FEED) {
        requestFeeds(mCircleId, 1);
      }
    }
  }

  @Override
  protected void onActive() {
    if (mLvFeed != null) mLvFeed.setAdapter(null);

    if (isAdded()) {
      if (mMode == MODE_REGION) {
        requestRegionFeeds(mRegionType, mDistance, 1);
      } else if (mMode == MODE_FEED) {
        requestFeeds(mCircleId, 1);
      }
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
          requestRegionFeeds(getRegionType(), mDistance, mPageInfo.currPage + 1);
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
          if (mMode == MODE_REGION) {
            requestRegionFeeds(mRegionType, mDistance, 1);
          } else if (mMode == MODE_FEED) {
            requestFeeds(mCircleId, 1);
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

  @Deprecated
  public int getCircleId() {
    return mCircle == null ? 0 : mCircle.id;
  }

  public void refresh() {
    getBaseActivity().showProgressBar();
    if (isAdded()) {
      if (mMode == MODE_REGION) {
        requestRegionFeeds(mRegionType, mDistance, 1);
      } else if (mMode == MODE_FEED) {
        requestFeeds(mCircleId, 1);
      }
    }
  }


  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      updateTitleBar();
    }
  }

  public void updateTitleBar() {
    if (mMode == MODE_FEED) {
      getTopBar().setTitleTabSelected(0);
      getTopBar().setSubTitle(String.format("%s | %s", mCircle.shortName, mSubInfo == null ? "" : mSubInfo));
      getTopBar().setTitleTabText(0, "关注");

      if (mCircle.snapshot == 1) {
        getTopBar().getAbRight().setText(getString(R.string.snapshot));
        getTopBar().getAbRight().setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                SnapshotActivity.start(getActivity(), mCircle);
              }
            }
        );
      } else {
        getTopBar().getAbRight().hide();
      }
    } else if (mRegionType == 0) {
      getTopBar().setTitleTabSelected(0);
      getTopBar().setSubTitle(String.format("%s | %s", mCircle.shortName, mSubInfo == null ? "" : mSubInfo));
      getTopBar().setTitleTabText(0, "在职");

      if (mCircle.snapshot == 1) {
        getTopBar().getAbRight().setText(getString(R.string.snapshot));
        getTopBar().getAbRight().setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                SnapshotActivity.start(getActivity(), mCircle);
              }
            }
        );
      } else {
        getTopBar().getAbRight().hide();
      }
    } else if (mRegionType == 4 || mRegionType == 3) {
      getTopBar().setTitleTabSelected(1);
      getTopBar().setSubTitle(mSubInfo == null ? "" : mSubInfo);
      getTopBar().getAbRight().hide();
    }
  }

  protected abstract void requestRegionFeeds(int regionType, int distance, int page);

  protected abstract void requestFeeds(int circleId, int page);

  protected void responseForFeedsByRegionRequest(FeedsByRegionResponse response, int page) {
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
        if (mRegionType == 4) {
          mDistance = response.object.regionRadius;
        }

        U.getBus().post(new RegionResponseEvent(mRegionType, mDistance));
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPageInfo = response.object.posts.page;
      mSubInfo = response.object.subInfo;
      updateTitleBar();

      if (response.object.fetch != null) {
        int count = 0;
        if (response.object.fetch.newComment != null) {
          count += response.object.fetch.newComment.unread;
        }

        if (response.object.fetch.myPostComment != null) {
          count += response.object.fetch.myPostComment.unread;
        }

        Account.inst().setNewCommentCount(count);

        if (response.object.fetch.newPraise != null) {
          Account.inst().setHasNewPraise(response.object.fetch.newPraise.praise == 1);
          U.getBus().post(new UpdatePraiseCountEvent(response.object.fetch.newPraise.praiseCount,
              response.object.fetch.newPraise.percent));
        }

        if (getRegionType() == 0 && mCircle != null) {
          U.getBus().post(new NewAllPostCountEvent(mCircle.id, response.object.fetch.newPostAllCount));
          U.getBus().post(new NewHotPostCountEvent(mCircle.id, response.object.fetch.newPostHotCount));
          U.getBus().post(new NewFriendsPostCountEvent(mCircle.id, response.object.fetch.newPostFriendsCount));
        }
      }

      if (getRegionType() == 0) {
        FetchNotificationService.setCircleId(mCircle == null ? 0 : mCircle.id);
      } else {
        FetchNotificationService.setCircleId(0);
      }
    } else {
      cacheOutFeedsByRegion(mRegionType, mDistance, page);
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected void responseForFeedsRequest(FeedsResponse response, int page) {
    if (RESTRequester.responseOk(response)) {
      if (page == 1) {
        mCircle = response.object.circle;

        U.getBus().post(new CircleResponseEvent(mCircle));

        M.getRegisterHelper().unregister(mFeedAdapter);
        mFeedAdapter = new FeedRegionAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
        }

      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPageInfo = response.object.posts.page;
      mSubInfo = response.object.subInfo;
      updateTitleBar();

      if (response.object.fetch != null) {
        int count = 0;
        if (response.object.fetch.newComment != null) {
          count += response.object.fetch.newComment.unread;
        }

        if (response.object.fetch.myPostComment != null) {
          count += response.object.fetch.myPostComment.unread;
        }

        Account.inst().setNewCommentCount(count);

        if (response.object.fetch.newPraise != null) {
          Account.inst().setHasNewPraise(response.object.fetch.newPraise.praise == 1);
          U.getBus().post(new UpdatePraiseCountEvent(response.object.fetch.newPraise.praiseCount,
              response.object.fetch.newPraise.percent));
        }

        if (getRegionType() == 0 && mCircle != null) {
          U.getBus().post(new NewAllPostCountEvent(mCircle.id, response.object.fetch.newPostAllCount));
          U.getBus().post(new NewHotPostCountEvent(mCircle.id, response.object.fetch.newPostHotCount));
          U.getBus().post(new NewFriendsPostCountEvent(mCircle.id, response.object.fetch.newPostFriendsCount));
        }
      }

      FetchNotificationService.setCircleId(mCircle == null ? 0 : mCircle.id);
    } else {
      cacheOutFeeds(mCircle.id, page);
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected abstract void cacheOutFeedsByRegion(int regionType, int distance, int page);

  protected abstract void cacheOutFeeds(int circle, int page);

  protected void responseForFeedsByRegionCache(FeedsByRegionResponse response, int page) {
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
        if (mRegionType == 4) {
          mDistance = response.object.regionRadius;
        } else if (mRegionType == 0) {
          getBaseActivity().getTopBar().setTitleTabText(0, "在职");
        }

        U.getBus().post(new RegionResponseEvent(getRegionType(), mDistance));
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

  protected void responseForFeedsCache(FeedsResponse response, int page) {
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

      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }

      mPageInfo = response.object.posts.page;
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
