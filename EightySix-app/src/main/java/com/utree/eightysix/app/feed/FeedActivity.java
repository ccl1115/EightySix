package com.utree.eightysix.app.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.PraiseStaticActivity;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.publish.FeedbackActivity;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.settings.MainSettingsActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.request.MyCirclesRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.TopBar;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

  @InjectView (R.id.ll_side)
  public LinearLayout mLlSide;

  @InjectView (R.id.v_mask)
  public View mVMask;

  private FeedAdapter mFeedAdapter;
  private SideCirclesAdapter mSideCirclesAdapter;
  private PopupWindow mPopupMenu;
  private LinearLayout mMenu;
  private boolean mSideShown;
  private Circle mCircle;
  private List<Circle> mSideCircles;

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);
    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle, ArrayList<Circle> side) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);
    intent.putParcelableArrayListExtra("side", side);
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

  @OnClick (R.id.tv_more)
  public void onSideMoreClicked() {
    startActivity(new Intent(this, BaseCirclesActivity.class));
  }

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null) return;
    PostActivity.start(this, (Post) item);
  }

  @OnItemClick (R.id.lv_side_circles)
  public void onLvSideItemClicked(int position) {
    Circle circle = mSideCircles.get(position);
    if (circle != null && !circle.selected) {
      mCircle = circle;
      mLvFeed.setAdapter(null);
      for (Circle c : mSideCircles) {
        c.selected = false;
      }
      mCircle.selected = true;
      U.getBus().post(new AdapterDataSetChangedEvent());
      refresh();
    }
    hideSide();
  }

  @OnClick (R.id.v_mask)
  public void onVMaskClicked() {
    hideSide();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setFillContent(true);


    onNewIntent(getIntent());


    if (Env.firstRun(FIRST_RUN_KEY)) {
      //showSide();
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
        if (U.useFixture()) {
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
          startActivity(new Intent(FeedActivity.this, MsgActivity.class));
        } else if (position == 1) {
          refresh();
        }
      }

      @Override
      public int getCount() {
        return 2;
      }

      @Override
      public ViewGroup.LayoutParams getLayoutParams(int position) {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();

    U.getBus().register(mLvFeed);
    U.getBus().register(mLvSideCircles);
  }

  @Override
  protected void onPause() {
    super.onPause();

    U.getBus().unregister(mLvFeed);
    U.getBus().unregister(mLvSideCircles);
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
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  protected void onNewIntent(Intent intent) {
    //region 标题栏数据处理
    Circle circle = intent.getParcelableExtra("circle");

    if (circle == null || !circle.equals(mCircle)) {
      mLvFeed.setAdapter(null);
    }

    mCircle = circle;

    if (mCircle == null && U.useFixture()) {
      mCircle = U.getFixture(Circle.class, "valid");
    }

    if (mCircle == null) {
      showToast(R.string.circle_not_found, false);
    } else {
      setTitle();
    }
    //endregion


    //region 侧边栏数据处理
    List<Circle> circles = intent.getParcelableArrayListExtra("side");

    if (circles != null) {
      mSideCircles = circles;
    }

    if (mSideCircles != null) {
      for (Iterator<Circle> iterator = mSideCircles.iterator(); iterator.hasNext(); ) {
        Circle c = iterator.next();
        if (c == null) iterator.remove();
      }
      selectSideCircle(mSideCircles);
      mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
      mLvSideCircles.setAdapter(mSideCirclesAdapter);
    } else if (U.useFixture()) {
      mSideCircles = U.getFixture(Circle.class, 10, "valid");
      selectSideCircle(mSideCircles);
      mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
      mLvSideCircles.setAdapter(mSideCirclesAdapter);
    } else {
      cacheOutSideCircle();
    }
    //endregion


    if (U.useFixture()) {
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          mFeedAdapter = new FeedAdapter(U.getFixture(Post.class, 20, "valid"), mCircle.lock == 1);
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

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        hideSide();
      }
    }, 1000);
  }

  @Override
  public void onBackPressed() {
    if (mSideShown) {
      hideSide();
    } else {
      moveTaskToBack(true);
    }
  }

  private void selectSideCircle(List<Circle> sideCircles) {
    if (sideCircles != null) {
      for (Circle c : sideCircles) {
        c.selected = false;
      }

      if (mCircle == null) return;

      for (Circle c : sideCircles) {
        if (mCircle.name.equals(c.name)) {
          c.selected = true;
          break;
        }
      }
    }
  }

  private void refresh() {
    setTitle();
    if (U.useFixture()) {
      showProgressBar();
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          mFeedAdapter = new FeedAdapter(U.getFixture(Post.class, 20, "valid"), mCircle.lock == 1);
          mLvFeed.setAdapter(mFeedAdapter);
          U.getBus().post(new ListViewScrollStateIdledEvent());
          if (mSideShown) {
            mLvFeed.setSelection(1);
          }
          hideProgressBar();
        }
      }, 2000);
    }
  }

  private void setTitle() {
    if (mCircle == null) return;

    setTopTitle(mCircle.shortName);
    setTopSubTitle(String.format(getString(R.string.friends_info), mCircle.friendCount, mCircle.workmateCount));
    if (mCircle.lock == 1) {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_lock_small), null, null, null);
      getTopBar().mSubTitle.setCompoundDrawablePadding(U.dp2px(5));
    } else {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
  }

  private void showSide() {
    hideTopBar(true);
    mSideShown = true;

    mLlSide.setVisibility(View.VISIBLE);
    mVMask.setVisibility(View.VISIBLE);
    AnimatorSet set = new AnimatorSet();
    set.playTogether(
        ObjectAnimator.ofFloat(mLlSide, "translationX", -mLvSideCircles.getMeasuredWidth(), 0f),
        ObjectAnimator.ofFloat(mVMask, "alpha", 0f, 1f)
    );
    set.setDuration(200);
    set.start();
  }

  private void hideSide() {
    showTopBar(true);
    mSideShown = false;

    AnimatorSet set = new AnimatorSet();
    set.playTogether(
        ObjectAnimator.ofFloat(mLlSide, "translationX", 0f, -mLlSide.getMeasuredWidth()),
        ObjectAnimator.ofFloat(mVMask, "alpha", 1f, 0f)
    );
    set.setDuration(200);
    set.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mLlSide.setVisibility(View.INVISIBLE);
        mVMask.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    set.start();
  }

  private void cacheOutSideCircle() {
    cacheOut(new MyCirclesRequest("", 1), new OnResponse<CirclesResponse>() {
      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0) {
          mSideCircles = response.object.lists.subList(0, 10);
          selectSideCircle(mSideCircles);
          mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
          mLvSideCircles.setAdapter(mSideCirclesAdapter);
        } else {
          requestSideCircle();
        }
      }
    }, CirclesResponse.class);
  }

  private void cacheInSideCircle(String string) {
    cacheIn(new MyCirclesRequest("", 1), string);
  }

  private void requestSideCircle() {
    request(new MyCirclesRequest("", 1), new OnResponse<CirclesResponse>() {

      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0) {
          mSideCircles = response.object.lists.subList(0, 10);
          selectSideCircle(FeedActivity.this.mSideCircles);
          mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
          mLvSideCircles.setAdapter(mSideCirclesAdapter);
        }
      }
    }, CirclesResponse.class);
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
      startActivity(new Intent(FeedActivity.this, PraiseStaticActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_feedback)
    void onLlFeedbackClicked() {
      startActivity(new Intent(FeedActivity.this, FeedbackActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_about)
    void onLlAboutClicked() {
      showToast("TODO about");
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_settings)
    void onLlSettingsClicked() {
      startActivity(new Intent(FeedActivity.this, MainSettingsActivity.class));
      mPopupMenu.dismiss();
    }
  }
}