package com.utree.eightysix.app.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
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
  private PopupWindow mPopupMenu;
  private LinearLayout mMenu;
  private boolean mSideShown;
  private Circle mCircle;

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);
    context.startActivity(intent);
  }

  public static void start(Context context, int id) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    context.startActivity(intent);
  }

  @OnClick (R.id.ib_send)
  public void onSendClicked() {
    startActivity(new Intent(this, PublishActivity.class));
  }

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position) {
    if (mFeedAdapter.getItemViewType(position) == FeedAdapter.TYPE_POST) {
      PostActivity.start(this, mFeedAdapter.getItem(position));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setFillContent(true);

    mCircle = (Circle) getIntent().getSerializableExtra("circle");

    if (mCircle == null && BuildConfig.DEBUG) {
      mCircle = U.getFixture(Circle.class, "valid");
    }

    if (mCircle == null) {
      showToast(R.string.circle_not_found, false);
    } else {
      setTopTitle(mCircle.name);
      setTopSubTitle(String.format(getString(R.string.friends_info), mCircle.friendCount, mCircle.workmateCount));
    }

    if (BuildConfig.DEBUG) {
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          mFeedAdapter = new FeedAdapter(U.getFixture(Post.class, 20, "valid"));
          mLvFeed.setAdapter(mFeedAdapter);
          U.getBus().post(new ListViewScrollStateIdledEvent());
          hideProgressBar();
          if (mSideShown) {
            mLvFeed.setSelection(1);
          }
        }
      }, 2000);
      showProgressBar();
    } else {
      // TODO request data
    }

    mSideCirclesAdapter = new SideCirclesAdapter(U.getFixture(Circle.class, 10, "valid"));
    mLvSideCircles.setAdapter(mSideCirclesAdapter);

    if (Env.firstRun(FIRST_RUN_KEY)) {
        showSide();
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
        if (view.getFirstVisiblePosition() <= 1) {
          showTopBar(true);
        }
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
        if (BuildConfig.DEBUG) {
          getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
              mFeedAdapter.add(U.getFixture(Post.class, 20, "valid"));
              mLvFeed.stopLoadMore();
            }
          }, 2000);
          return true;
        } else {
          // TODO request more data
          return false;
        }
      }
    });


    setActionLeftDrawable(null);

    getTopBar().setOnActionOverflowClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mPopupMenu == null) {
          mMenu = (LinearLayout) View.inflate(FeedActivity.this, R.layout.widget_feed_menu, null);
          mPopupMenu = new PopupWindow(mMenu, dp2px(190), dp2px(225) + 4);
          new MenuViewHolder(mMenu);
          mPopupMenu.setFocusable(true);
          mPopupMenu.setOutsideTouchable(true);
          mPopupMenu.setBackgroundDrawable(new BitmapDrawable(getResources()));
        }

        mPopupMenu.showAsDropDown(getTopBar().mActionOverFlow);
      }
    });

    getTopBar().mTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
        getResources().getDrawable(R.drawable.top_bar_arrow_down), null);


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

    U.getBus().register(mLvFeed);
  }

  @Override
  protected void onPause() {
    super.onPause();

    U.getBus().unregister(mLvFeed);
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

  @Keep
  class MenuViewHolder {


    MenuViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.ll_introduce)
    void onLlIntroduceClicked() {
      showToast("TODO introduce");
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_praise_count)
    void onLlPraiseCountClicked() {
      showToast("TODO praise count");
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_feedback)
    void onLlFeedbackClicked() {
      showToast("TODO feedback");
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_about)
    void onLlAboutClicked() {
      showToast("TODO about");
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_settings)
    void onLlSettingsClicked() {
      showToast("TODO settings");
      mPopupMenu.dismiss();
    }
  }
}