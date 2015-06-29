package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.app.region.FeedRegionAdapter;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Feeds;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
public abstract class AbsFeedsFragment extends BaseFragment {
  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView (R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView (R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  @InjectView (R.id.ll_sub_title)
  public LinearLayout mLlSubTitle;

  @InjectView (R.id.tv_sub_info)
  public TextView mTvSubInfo;

  @InjectView (R.id.iv_icon)
  public ImageView mIvIcon;

  @InjectView (R.id.tv_title)
  public TextView mTvTitle;

  @InjectView (R.id.rb_count)
  public RoundedButton mRbCount;

  protected FeedRegionAdapter mFeedAdapter;

  protected Circle mCircle;

  protected boolean mPostPraiseRequesting;
  private int mLastFirstVisibleItem;
  private OnScrollListener mOnScrollListener;
  private boolean mHidden;

  protected int mPage = 1;
  protected boolean mHasMore = true;
  private boolean mLlSubTitleHidden;
  private ObjectAnimator mHideLlSubTitleAnimator;
  private ObjectAnimator mShowLlSubTitleAnimator;

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
      request();
    }
  }

  @Override
  protected void onActive() {
    if (mLvFeed != null) mLvFeed.setAdapter(null);

    if (isAdded()) {
      request();
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
    if (isAdded()) {
      request();
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    mHidden = hidden;
    if (!mHidden) {
      updateTitleBar();
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

    initAnimator();

    mLvFeed.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mHasMore;
      }

      @Override
      public boolean onLoadMoreStart() {
        onLoadMore(mPage + 1);
        mPage += 1;
        request();
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
          request();
        }
      }

      @Override
      public void onDrag(int value) {
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
          } else if ((post.viewType == 1 || post.viewType == 2 || post.viewType == 5) && Env.firstRun("overlay_tip_source")) {
            mFeedAdapter.showTipSource(firstItem);
          } else if (Env.firstRun("overlay_tip_praise")) {
            mFeedAdapter.showTipPraise(firstItem);
          } else if (post.tags != null && post.tags.size() > 0 && Env.firstRun("overlay_tip_tags")) {
            mFeedAdapter.showTipTags(firstItem);
          } else if (!TextUtils.isEmpty(post.topicPrev) && Env.firstRun("overlay_tip_topic")) {
            mFeedAdapter.showTipTopic(firstItem);
          }
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mHidden) return;

        if (firstVisibleItem > 2) {
          if (firstVisibleItem > mLastFirstVisibleItem) {
            getBaseActivity().hideTopBar(true);
            if (mOnScrollListener != null) {
              mOnScrollListener.onHideTopBar();
              hideLlSubTitle();
            }
          } else if (firstVisibleItem < mLastFirstVisibleItem) {
            getBaseActivity().showTopBar(true);
            if (mOnScrollListener != null) {
              mOnScrollListener.onShowTopBar();
              showLlSubTitle();
            }
          }
        } else {
          getBaseActivity().showTopBar(true);
          if (mOnScrollListener != null) {
            mOnScrollListener.onShowTopBar();
          }
        }
        mLastFirstVisibleItem = firstVisibleItem;
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

  public void setOnScrollListener(OnScrollListener listener) {
    mOnScrollListener = listener;
  }

  protected abstract void updateTitleBar();

  protected abstract void request();

  protected abstract void cacheOut();

  protected abstract void onPullRefresh();

  protected abstract void onLoadMore(int page);

  boolean canPublish() {
    if (mFeedAdapter != null) {
      Feeds feeds = mFeedAdapter.getFeeds();
      if (feeds != null) return feeds.current == 1 || feeds.lock == 0;
    }
    return false;
  }

  public final void hideLlSubTitle() {
    if (mLlSubTitleHidden || mHideLlSubTitleAnimator.isRunning()) return;
    if (mShowLlSubTitleAnimator.isRunning()) {
      mShowLlSubTitleAnimator.cancel();
    }
    mHideLlSubTitleAnimator.start();
  }

  public final void showLlSubTitle() {
    if (!mLlSubTitleHidden || mShowLlSubTitleAnimator.isRunning()) return;
    if (mHideLlSubTitleAnimator.isRunning()) {
      mHideLlSubTitleAnimator.cancel();
    }
    mShowLlSubTitleAnimator.start();
  }

  private void initAnimator() {
    mHideLlSubTitleAnimator = ObjectAnimator.ofFloat(mLlSubTitle, "translationY", 0,
        -getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height));
    mHideLlSubTitleAnimator.setDuration(150);
    mHideLlSubTitleAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mLlSubTitleHidden = true;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    mShowLlSubTitleAnimator = ObjectAnimator.ofFloat(mLlSubTitle, "translationY",
        -getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height), 0f);
    mShowLlSubTitleAnimator.setDuration(150);
    mShowLlSubTitleAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mLlSubTitleHidden = false;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
  }


  public interface OnScrollListener {

    void onShowTopBar();

    void onHideTopBar();
  }
}
