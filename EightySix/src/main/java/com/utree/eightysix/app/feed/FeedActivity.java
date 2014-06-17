package com.utree.eightysix.app.feed;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.publish.PostActivity;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.response.data.Post;
import com.utree.eightysix.rest.FixtureUtil;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RefreshIndicator;

/**
 */
@Layout (R.layout.activity_feed)
public class FeedActivity extends BaseActivity {

  private static final int PW_CIRCLE_SELECTOR_WIDTH = 190; // dp
  private static final int PW_CIRCLE_SELECTOR_HEIGHT = 200; // dp

  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView (R.id.ib_send)
  public ImageButton mSend;

  public PopupWindow mPWCircleSelector;
  public LinearLayout mLLCircleSelector;

  private FeedAdapter mFeedAdapter;
  private int mCircleSelectorWidth;
  private int mCircleSelectorHeight;

  @OnClick (R.id.ib_send)
  public void onSendClicked() {
    startActivity(new Intent(this, PostActivity.class));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setFillContent(true);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mFeedAdapter = new FeedAdapter(FixtureUtil.from(Post.class).<Post>gimme(20, "valid"));
        mLvFeed.setAdapter(mFeedAdapter);
        U.getBus().post(new ListViewScrollStateIdledEvent());
        hideProgressBar();
      }
    }, 2000);

    mCircleSelectorWidth = dp2px(PW_CIRCLE_SELECTOR_WIDTH);
    mCircleSelectorHeight = dp2px(PW_CIRCLE_SELECTOR_HEIGHT);

    mLvFeed.setOnScrollListener(new AbsListView.OnScrollListener() {

      private int mPreFirstVisibleItem;

      private AnimatorSet mDownSet = new AnimatorSet();
      private AnimatorSet mUpSet = new AnimatorSet();

      private boolean mIsDown = false;
      private boolean mIsUp = true;

      {
        mDownSet.setDuration(500);
        mDownSet.playTogether(
            ObjectAnimator.ofFloat(mSend, "translationY", 0f, 200f)
        );
        mDownSet.addListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {

          }

          @Override
          public void onAnimationEnd(Animator animation) {
            mIsDown = true;
            mIsUp = false;
          }

          @Override
          public void onAnimationCancel(Animator animation) {

          }

          @Override
          public void onAnimationRepeat(Animator animation) {

          }
        });

        mUpSet.setDuration(500);
        mUpSet.playTogether(
            ObjectAnimator.ofFloat(mSend, "translationY", 200f, 0f)
        );
        mUpSet.addListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {

          }

          @Override
          public void onAnimationEnd(Animator animation) {
            mIsUp = true;
            mIsDown = false;
          }

          @Override
          public void onAnimationCancel(Animator animation) {

          }

          @Override
          public void onAnimationRepeat(Animator animation) {

          }
        });
      }

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
          U.getBus().post(new ListViewScrollStateIdledEvent());
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem >= 1) {
          if (firstVisibleItem > mPreFirstVisibleItem) {
            if (!mDownSet.isRunning() && !mIsDown) {
              mDownSet.start();
              hideTopBar(true);
            }
          } else if (firstVisibleItem < mPreFirstVisibleItem) {
            if (!mUpSet.isRunning() && !mIsUp) {
              mUpSet.start();
              showTopBar(true);
            }
          }
          mPreFirstVisibleItem = firstVisibleItem;
        }
      }

    });

    mLvFeed.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView() {
        return View.inflate(FeedActivity.this, R.layout.footer_load_more, null);
      }

      @Override
      public boolean hasMore() {
        return true;
      }

      @Override
      public boolean onLoadMoreStart() {
        getHandler().postDelayed(new Runnable() {
          @Override
          public void run() {
            mFeedAdapter.add(FixtureUtil.from(Post.class).<Post>gimme(20, "valid"));
            mLvFeed.stopLoadMore();
          }
        }, 2000);
        return true;
      }
    });

    showProgressBar();

    setActionLeftDrawable(null);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onActionLeftOnClicked() {
    if (mPWCircleSelector == null) {
      mLLCircleSelector = (LinearLayout) View.inflate(FeedActivity.this,
          R.layout.widget_popup_circle_selector, null);
      mPWCircleSelector = new PopupWindow(mLLCircleSelector,
          mCircleSelectorWidth, mCircleSelectorHeight, true);
      mPWCircleSelector.setOutsideTouchable(false);
      mPWCircleSelector.setBackgroundDrawable(new BitmapDrawable());
    }
    if (!mPWCircleSelector.isShowing()) {
      mPWCircleSelector.showAsDropDown(getTopBar());
    }
  }
}