package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.feed.event.UpdatePraiseCountEvent;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.data.*;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.request.FeedsRequest;
import com.utree.eightysix.request.PostPraiseCancelRequest;
import com.utree.eightysix.request.PostPraiseRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.guide.Guide;

import java.util.Iterator;

/**
 * @author simon
 */
public class FeedFragment extends BaseFragment {

  @InjectView(R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView(R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  private FeedAdapter mFeedAdapter;
  private Circle mCircle;
  private Paginate.Page mPageInfo;

  private boolean mRefreshed;

  private Guide mSourceTip;
  private Guide mPraiseTip;
  private Guide mShareTip;
  private int mWorkerCount;
  private boolean mPostPraiseRequesting;

  public FeedFragment() {
  }

  @OnItemClick(R.id.lv_feed)
  public void onLvFeedItemClicked(int position, View view) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null || !(item instanceof Post)) return;
    PostActivity.start(getActivity(), (Post) item);
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
        U.getAnalyser().trackEvent(getActivity(), "feed_load_more", String.valueOf(mPageInfo.currPage + 1));
        if (mRefreshed) {
          requestFeeds(mCircle.id, mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        } else {
          cacheOutFeeds(mCircle.id, mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        }
        return true;
      }
    });

    M.getRegisterHelper().register(mLvFeed);

    mRefresherView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        U.getAnalyser().trackEvent(getActivity(), "feed_pull_refresh", "feed_pull_refresh");
        mRefreshed = true;
        if (mCircle != null) {
          requestFeeds(mCircle.id, 1);
        } else {
          requestFeeds(0, 1);
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

  public boolean onBackPressed() {
    return hidePraiseTip() || hideSourceTip() || hideShareTip();
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

  public void setCircle(Circle circle) {
    if (circle == null || !circle.equals(mCircle)) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    mCircle = circle;

    if (mCircle == null) {
      if (U.useFixture()) {
        mCircle = U.getFixture(Circle.class, "valid");
      } else {
        mCircle = Env.getLastCircle();
      }
    }

    if (mCircle != null) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);

      if (isAdded()) {
        cacheOutFeeds(mCircle.id, 1);
      }
    }
  }

  public void setCircle(int id) {
    if (mCircle != null && mCircle.id != id) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    if (isAdded()) {
      cacheOutFeeds(id, 1);
    }
  }

  public void setCircle(int id, boolean skipCache) {
    if (mCircle != null && mCircle.id != id) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    if (isAdded()) {
      if (skipCache) {
        requestFeeds(id, 1);
      } else {
        cacheOutFeeds(id, 1);
      }
    }
  }

  public void setCircle(Circle circle, boolean skipCache) {
    if (circle == null || !circle.equals(mCircle)) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    mCircle = circle;

    if (mCircle == null) {
      mCircle = Env.getLastCircle();
    }

    if (mCircle != null) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);

      if (isAdded()) {
        if (skipCache) {
          requestFeeds(mCircle.id, 1);
        } else {
          cacheOutFeeds(mCircle.id, 1);
        }
      }
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (mCircle != null) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);

      if (isAdded()) {
        cacheOutFeeds(mCircle.id, 1);
      }
    } else {
      if (isAdded()) {
        requestFeeds(0, 1);
      }
    }
  }

  public int getCurrFriends() {
//    if (mLvFeed != null && mFeedAdapter.getFeeds() != null) {
//      return mFeedAdapter.getFeeds().circle.friendCount;
//    } else {
//      return 0;
//    }
//
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
    mRefreshed = true;
    getBaseActivity().showProgressBar();
    if (mCircle != null) {
      requestFeeds(mCircle.id, 1);
    } else {
      requestFeeds(0, 1);
    }
  }

  @Subscribe
  public void onFeedPostPraiseEvent(final FeedPostPraiseEvent event) {
    if (mPostPraiseRequesting) {
      return;
    }
    mPostPraiseRequesting = true;
    if (event.isCancel()) {
      getBaseActivity().request(new PostPraiseCancelRequest(event.getPost().id), new OnResponse2<Response>() {
        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            U.getBus().post(event.getPost());
          } else if ((response.code & 0xffff) == 0x2286) {
            event.getPost().praised = 0;
          } else {
            event.getPost().praised = 0;
            event.getPost().praise++;
          }
          mFeedAdapter.notifyDataSetChanged();

          mPostPraiseRequesting = false;
        }

        @Override
        public void onResponseError(Throwable e) {
          mPostPraiseRequesting = false;
        }
      }, Response.class);
    } else {
      getBaseActivity().request(new PostPraiseRequest(event.getPost().id), new OnResponse2<Response>() {
        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            U.getBus().post(event.getPost());
          } else if ((response.code & 0xffff) == 0x2286) {
            event.getPost().praised = 1;
          } else {
            event.getPost().praised = 1;
            event.getPost().praise = Math.max(0, event.getPost().praise - 1);
          }
          mFeedAdapter.notifyDataSetChanged();

          mPostPraiseRequesting = false;
        }

        @Override
        public void onResponseError(Throwable e) {
          mPostPraiseRequesting = false;
        }
      }, Response.class);
    }
  }

  @Subscribe
  public void onPostPublishedEvent(PostPublishedEvent event) {
    if (mFeedAdapter != null) {
      if (mCircle != null && mCircle.id == event.getCircleId()) {
        mFeedAdapter.add(event.getPost());
        mRstvEmpty.setVisibility(View.INVISIBLE);
        mLvFeed.setSelection(0);
      }
    }
  }

  @Subscribe
  public void onPostDeleteEvent(PostDeleteEvent event) {
    if (mFeedAdapter == null || mFeedAdapter.getFeeds() == null) return;
    for (Iterator<BaseItem> iterator = mFeedAdapter.getFeeds().posts.lists.iterator(); iterator.hasNext(); ) {
      BaseItem item = iterator.next();
      if (item != null && item instanceof Post) {
        Post p = ((Post) item);
        if (p.equals(event.getPost())) {
          iterator.remove();
          mFeedAdapter.notifyDataSetChanged();
          break;
        }
      }
    }
  }

  @Subscribe
  public void onContactsSyncEvent(ContactsSyncEvent event) {
    if (mFeedAdapter.getFeeds().upContact == 0) {
      if (event.isSucceed()) {
        U.showToast("上传通讯录成功");
      } else {
        U.showToast("上传通讯录失败");
      }
    }

    refresh();
    getBaseActivity().hideProgressBar();
  }


  public int getWorkerCount() {
    if (mFeedAdapter != null && mFeedAdapter.getFeeds() != null) {
      return mFeedAdapter.getFeeds().workerCount;
    } else {
      return 0;
    }
  }

  private void requestFeeds(final int id, final int page) {
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().setTopSubTitle("");
    }
    getBaseActivity().request(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mCircle = response.object.circle;
            U.getBus().post(mCircle);

            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
            }

            mFeedAdapter = new FeedAdapter(response.object);
            M.getRegisterHelper().register(mFeedAdapter);
            mLvFeed.setAdapter(mFeedAdapter);

            ((FeedActivity) getBaseActivity()).setTitle(mCircle);
            getBaseActivity().setTopSubTitle(String.format(getString(R.string.friends_info),
                mCircle.friendCount, response.object.workerCount));
          } else if (mFeedAdapter != null) {
            mFeedAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;

          ((FeedActivity) getBaseActivity()).mSend.setImageResource(response.object.lock != 1 || response.object.current == 1 ?
              R.drawable.ic_post_pen : R.drawable.ic_post_pen_disabled);


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
        } else {
          mRstvEmpty.setVisibility(View.VISIBLE);
          if (mCircle != null) {
            getBaseActivity().setTopTitle(mCircle.shortName);
          }
          getBaseActivity().setTopSubTitle("");
        }
        mRefresherView.setRefreshing(false);
        mLvFeed.stopLoadMore();
        getBaseActivity().hideProgressBar();
        getBaseActivity().hideRefreshIndicator();
      }
    }, FeedsResponse.class);
  }

  private void cacheOutFeeds(final int id, final int page) {
    getBaseActivity().cacheOut(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          if (page == 1) {
            mCircle = response.object.circle;
            U.getBus().post(mCircle);

            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            } else {
              mRstvEmpty.setVisibility(View.INVISIBLE);
            }

            mFeedAdapter = new FeedAdapter(response.object);
            M.getRegisterHelper().register(mFeedAdapter);
            mLvFeed.setAdapter(mFeedAdapter);

            ((FeedActivity) getBaseActivity()).setTitle(mCircle);
          } else if (mFeedAdapter != null) {
            mFeedAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;

          ((FeedActivity) getBaseActivity())
              .setMyPraiseCount(response.object.myPraiseCount, response.object.praisePercent, response.object.upDown);

          ((FeedActivity) getBaseActivity()).mSend.setImageResource(response.object.lock != 1 || response.object.current == 1 ?
              R.drawable.ic_post_pen : R.drawable.ic_post_pen_disabled);
        } else {
          requestFeeds(id, page);
        }
        mRefresherView.setRefreshing(false);
        mLvFeed.stopLoadMore();
        getBaseActivity().hideProgressBar();
        getBaseActivity().hideRefreshIndicator();
      }
    }, FeedsResponse.class);
  }

  boolean hidePraiseTip() {
    if (mPraiseTip != null) {
      if (mPraiseTip.isShowing()) {
        mPraiseTip.dismiss();
        return true;
      }
    }
    return false;
  }

  boolean hideSourceTip() {
    if (mSourceTip != null) {
      if (mSourceTip.isShowing()) {
        mSourceTip.dismiss();
        return true;
      }
    }
    return false;
  }

  boolean hideShareTip() {
    if (mShareTip != null) {
      if (mShareTip.isShowing()) {
        mShareTip.dismiss();
        return true;
      }
    }
    return false;
  }

  boolean canPublish() {
    if (mFeedAdapter != null) {
      Feeds feeds = mFeedAdapter.getFeeds();
      if (feeds != null) return feeds.current == 1 || feeds.lock == 0;
    }
    return false;
  }
}
