package com.utree.eightysix.app.feed;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
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
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.response.data.Circle;
import com.utree.eightysix.response.data.Post;
import com.utree.eightysix.rest.FixtureUtil;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout (R.layout.activity_feed)
public class FeedActivity extends BaseActivity {

  private static final String FIRST_RUN_KEY = "feed";

  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView (R.id.lv_side_circles)
  public AdvancedListView mLvSideCircles;

  @InjectView (R.id.ib_send)
  public ImageButton mSend;

  private FeedAdapter mFeedAdapter;
  private SideCirclesAdapter mSideCirclesAdapter;

  private boolean mSideShown;

  private Circle mCircle = FixtureUtil.from(Circle.class).gimme("valid");

  @OnClick (R.id.ib_send)
  public void onSendClicked() {

    startActivity(new Intent(this, PublishActivity.class));
  }

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position) {
    if (!mSideShown) {
      PostActivity.start(this, mFeedAdapter.getItem(position));
    }
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

    mSideCirclesAdapter = new SideCirclesAdapter(FixtureUtil.from(Circle.class).<Circle>gimme(10, "valid"));
    mLvSideCircles.setAdapter(mSideCirclesAdapter);

    if (Env.firstRun(FIRST_RUN_KEY)) {

    }

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

    getTopBar().setOnActionOverflowClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showToast("TODO show settings");
      }
    });

    getTopBar().mTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
        getResources().getDrawable(R.drawable.top_bar_arrow_down), null);

    setTopTitle(mCircle.name);
    setTopSubTitle(String.format(getString(R.string.friends_info), mCircle.friendCount, mCircle.workmateCount));

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return null;
      }

      @Override
      public Drawable getIcon(int position) {
        if (position == 0) {
          return getResources().getDrawable(R.drawable.ic_action_msg);
        } else if (position == 1) {
          return getResources().getDrawable(R.drawable.ic_action_refresh);
        }
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
      }

      @Override
      public void onClick(View view, int position) {
        if (position == 0) {
          showToast("TODO goto message center");
        } else if (position == 1) {
          showToast("TODO refresh");
        }
      }

      @Override
      public int getCount() {
        return 2;
      }
    });

    mLvFeed.setOnTouchListener(new View.OnTouchListener() {

      private boolean mHidden;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
          case MotionEvent.ACTION_DOWN:
            if (mSideShown) {
              mHidden = true;
              hideSide();
              return true;
            }
          case MotionEvent.ACTION_UP:
            final boolean b = mHidden;
            mHidden = false;
            return b;
        }

        return false;
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    Env.setFirstRun(FIRST_RUN_KEY, false);
    super.onDestroy();
  }

  @Override
  protected void onActionLeftOnClicked() {
    if (mSideShown) {
      hideSide();
    } else {
      showSide();
    }
  }

  @Override
  public void onBackPressed() {
    if (mSideShown) {
      hideSide();
    } else {
      super.onBackPressed();
    }
  }

  private void showSide() {
    hideTopBar(true);
    mSideShown = true;

    mLvSideCircles.setVisibility(View.VISIBLE);
    ObjectAnimator animator =
        ObjectAnimator.ofFloat(mLvSideCircles, "translationX", -mLvSideCircles.getMeasuredWidth(), 0);
    animator.setDuration(200);
    animator.start();
  }

  private void hideSide() {
    showTopBar(true);
    mSideShown = false;

    ObjectAnimator animator = ObjectAnimator.ofFloat(mLvSideCircles, "translationX", 0, -mLvSideCircles.getMeasuredWidth());
    animator.setDuration(200);
    animator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mLvSideCircles.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    animator.start();
  }
}