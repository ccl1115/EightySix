package com.utree.eightysix.app.feed;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.publish.PostActivity;
import com.utree.eightysix.widget.IRefreshable;
import com.utree.eightysix.widget.RefresherView;

/**
 */
@Layout (R.layout.activity_feed)
public class FeedActivity extends BaseActivity {

  private static final int PW_CIRCLE_SELECTOR_WIDTH = 190; // dp
  private static final int PW_CIRCLE_SELECTOR_HEIGHT = 200; // dp

  @InjectView (R.id.lv_feed)
  public ListView mLvFeed;

  @InjectView (R.id.ib_send)
  public ImageButton mSend;

  @InjectView (R.id.ib_refresh)
  public ImageButton mRefresh;

  @InjectView (R.id.rv_feed)
  public RefresherView mRvFeed;

  @InjectView (R.id.tv_head)
  public TextView mTvHead;

  @InjectView (R.id.tv_empty)
  public TextView mTvEmpty;

  public PopupWindow mPWCircleSelector;

  public LinearLayout mLLCircleSelector;

  private int mCircleSelectorWidth;
  private int mCircleSelectorHeight;

  @OnClick (R.id.ib_send)
  public void onSendClicked() {
    startActivity(new Intent(this, PostActivity.class));
  }

  @OnClick(R.id.ib_refresh)
  public void onRefreshClicked() {
    mRvFeed.refreshShowingHeader();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mLvFeed.setAdapter(new FeedAdapter());
      }
    }, 1000);

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
            ObjectAnimator.ofFloat(mSend, "translationY", 0f, 200f),
            ObjectAnimator.ofFloat(mRefresh, "translationY", 0f, 200f)
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
            ObjectAnimator.ofFloat(mSend, "translationY", 200f, 0f),
            ObjectAnimator.ofFloat(mRefresh, "translationY", 200f, 0f)
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

      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem > mPreFirstVisibleItem) {
          if (!mDownSet.isRunning() && !mIsDown) {
            mDownSet.start();
          }
        } else if (firstVisibleItem < mPreFirstVisibleItem) {
          if (!mUpSet.isRunning() && !mIsUp) {
            mUpSet.start();
          }
        }
        mPreFirstVisibleItem = firstVisibleItem;
      }
    });

    getTopBar().setOnActionLeftClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
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
    });

    mRvFeed.setOnRefreshListener(new IRefreshable.OnRefreshListener() {
      @Override
      public void onStateChanged(IRefreshable.State state) {
      }

      @Override
      public void onPreRefresh() {
      }

      @Override
      public void onRefreshData() {
        synchronized (this) {
          try {
            wait(2000);
          } catch (InterruptedException ignored) {
          }
        }
      }

      @Override
      public void onRefreshUI() {
      }
    });
  }
}