package com.utree.eightysix.app.tag;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.feed.FeedAdapter;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
public abstract class AbsTagFragment extends BaseFragment {
  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView(R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  protected FeedAdapter mFeedAdapter;
  protected Paginate.Page mPageInfo;

  private Tag mTag;

  protected boolean mPostPraiseRequesting;

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position, View view) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null || !(item instanceof Post)) return;
    PostActivity.start(getActivity(), (Post) item);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) requestFeeds(mTag.id, 1);
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
        }
        requestFeeds(mTag.id, mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        return true;
      }
    });

    M.getRegisterHelper().register(mLvFeed);

    mRefresherView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        onPullRefresh();
        if (isAdded()) {
          if (mTag != null) {
            requestFeeds(mTag.id, 1);
          } else {
            requestFeeds(0, 1);
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

  public void setTag(int id) {
    if (mTag != null && mTag.id != id) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
      mTag.id = id;
    } else {
      mTag = new Tag();
      mTag.id = id;
    }
  }

  public void setTag(Tag tag) {
    if (tag == null || !tag.equals(mTag)) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    mTag = tag;
  }

  @Override
  protected void onActive() {
    if (mTag != null) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);

      if (isAdded()) {
        requestFeeds(mTag.id, 1);
      }
    }
  }

  public int getTagId() {
    return mTag == null ? 0 : mTag.id;
  }

  public void refresh() {
    getBaseActivity().showProgressBar();
    if (mTag != null) {
      if (isAdded()) {
        requestFeeds(mTag.id, 1);
      }
    } else {
      if (isAdded()) {
        requestFeeds(0, 1);
      }
    }
  }


  protected abstract void requestFeeds(final int id, final int page);

  protected void responseForRequest(int circleId, FeedsResponse response, int page) {
    if (RESTRequester.responseOk(response)) {
      if (page == 1) {
        mFeedAdapter = new FeedAdapter(response.object);
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

    } else {
      cacheOutFeeds(circleId, page);
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected abstract void cacheOutFeeds(final int id, final int page);

  protected void responseForCache(FeedsResponse response, int page, int id) {
    if (response != null && response.code == 0 && response.object != null) {
      if (page == 1) {
        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.INVISIBLE);
        }

        mFeedAdapter = new FeedAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }
      mPageInfo = response.object.posts.page;
    } else {
      if (mFeedAdapter != null && mFeedAdapter.getCount() == 0) {
        mRstvEmpty.setVisibility(View.VISIBLE);
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

}
