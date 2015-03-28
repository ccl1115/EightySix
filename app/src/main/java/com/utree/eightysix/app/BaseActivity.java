package com.utree.eightysix.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.ButterKnife;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.utree.eightysix.C;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.settings.UpgradeDialog;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.event.LogoutListener;
import com.utree.eightysix.rest.*;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.TopBar;

import java.io.InputStream;
import java.util.Calendar;

import static com.nineoldandroids.view.ViewHelper.getTranslationY;

/**
 * Provides many base functionality to derived class
 * <p/>
 * <ul>
 * <li>To show toast using {@link #showToast}</li>
 * <li>To make api request using {@link #request}</li>
 * <li>Auto bind views and OnClickListener to annotated fields using annotation
 * {@link butterknife.InjectView} and {@link butterknife.OnClick}</li>
 * <li>Automatically set content view when annotated with {@link com.utree.eightysix.app.Layout}</li>
 * <li>An independent TopBar acts like the Android's ActionBar</li>
 * <li>Auto register and unregister to Otto's event bus</li>
 * <li>Automatically finish itself when LogoutEventFired, override onLogout() to prevent this</li>
 * </ul>
 */
public abstract class BaseActivity extends FragmentActivity implements LogoutListener, TopBar.Callback {

  private static boolean sBackground;

  private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      BaseActivity.this.onHandleMessage(msg);
    }
  };

  public View mVProgressMask;

  protected TopBar mTopBar;

  private FrameLayout mProgressBar;
  private LinearLayout mLlLoadingWrapper;
  private TextView mTvLoadingText;
  private ViewGroup mBaseView;
  private ObjectAnimator mHideTopBarAnimator;
  private ObjectAnimator mShowTopBarAnimator;
  private AnimatorSet mShowProgressBarAnimator;
  private AnimatorSet mHideProgressBarAnimator;

  private Toast mToast;
  private Toast mInActivityToast;

  private boolean mFillContent;

  protected boolean mResumed;

  public static boolean isBackground() {
    return sBackground;
  }

  @Override
  public final void setContentView(int layoutResID) {
    View content = mBaseView.findViewById(R.id.content);
    if (content != null) {
      mBaseView.removeView(content);
    }
    View inflate = LayoutInflater.from(this).inflate(layoutResID, mBaseView, false);
    inflate.setId(R.id.content);
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) inflate.getLayoutParams();
    params.topMargin = mFillContent ? 0 : getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);
    params.gravity = Gravity.TOP;

    mBaseView.addView(inflate, 0, params);

    ButterKnife.inject(this, this);

    TopTitle topTitle = getClass().getAnnotation(TopTitle.class);

    if (topTitle != null) {
      setTopTitle(getString(topTitle.value()));
    }
  }

  @Override
  public final void setContentView(View contentView) {
    View content = mBaseView.findViewById(R.id.content);
    if (content != null) {
      mBaseView.removeView(content);
    }
    contentView.setId(R.id.content);
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentView.getLayoutParams();
    if (params == null) {
      params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
    }
    params.topMargin = mFillContent ? 0 : getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);
    params.gravity = Gravity.TOP;

    mBaseView.addView(contentView, 0, params);

    ButterKnife.inject(this, this);

    TopTitle topTitle = getClass().getAnnotation(TopTitle.class);

    if (topTitle != null) {
      setTopTitle(getString(topTitle.value()));
    }
  }

  @Override
  public final void addContentView(View view, ViewGroup.LayoutParams params) {
    throw new RuntimeException("Call setContentView.");
  }

  public final void showProgressBar() {
    mProgressBar.setVisibility(View.VISIBLE);
    if (mShowProgressBarAnimator == null) {
      mShowProgressBarAnimator = new AnimatorSet();
      mShowProgressBarAnimator.playTogether(
          ObjectAnimator.ofFloat(mLlLoadingWrapper,
              "translationY",
              (getTranslationY(mLlLoadingWrapper) == 0) ?
                  mLlLoadingWrapper.getMeasuredHeight() : getTranslationY(mLlLoadingWrapper),
              0),
          ObjectAnimator.ofFloat(mLlLoadingWrapper, "alpha", 0f, 1f)
      );
      mShowProgressBarAnimator.setDuration(500);
    }
    if (mHideProgressBarAnimator != null) mHideProgressBarAnimator.cancel();
    mShowProgressBarAnimator.start();
  }

  public final void hideProgressBar() {
    if (mHideProgressBarAnimator == null) {
      mHideProgressBarAnimator = new AnimatorSet();
      mHideProgressBarAnimator.playTogether(
          ObjectAnimator.ofFloat(mLlLoadingWrapper,
              "translationY",
              getTranslationY(mLlLoadingWrapper),
              mLlLoadingWrapper.getMeasuredHeight()),
          ObjectAnimator.ofFloat(mLlLoadingWrapper, "alpha", 1f, 0f)
      );
      mHideProgressBarAnimator.setDuration(500);
      mHideProgressBarAnimator.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressBar.setVisibility(View.INVISIBLE);
          ViewHelper.setTranslationY(mLlLoadingWrapper, 0);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      });
    }
    if (mShowProgressBarAnimator != null) mShowProgressBarAnimator.cancel();
    mHideProgressBarAnimator.start();

    hideProgressMask();
  }

  public void setLoadingText(int res) {
    mTvLoadingText.setVisibility(View.VISIBLE);
    mTvLoadingText.setText(res);
  }

  public void setLoadingText(String text) {
    mTvLoadingText.setVisibility(View.VISIBLE);
    mTvLoadingText.setText(text);
  }

  public void setTopBarClickMode(int mode) {
    mTopBar.setTitleClickMode(mode);
  }

  public final <T extends Response> void request(Object request, OnResponse<T> onResponse, Class<T> clz) {
    RequestData data = new RequestData(request);
    U.getRESTRequester().request(request, new HandlerWrapper<T>(data, onResponse, clz));
  }

  public final <T extends Response> void cacheOut(Object request, OnResponse<T> onResponse, Class<T> clz) {
    RequestData data = new RequestData(request);
    new CacheOutWorker<T>(RESTRequester.genCacheKey(data.getApi(), data.getParams()), onResponse, clz).execute();
  }

  @Override
  public void onActionOverflowClicked() {

  }

  @Override
  public boolean showActionOverflow() {
    return false;
  }

  @Override
  public void onEnterSearch() {

  }

  @Override
  public void onExitSearch() {

  }

  @Override
  public void onSearchTextChanged(CharSequence cs) {

  }

  @Override
  public void onActionSearchClicked(CharSequence cs) {

  }

  @Override
  public void onTitleClicked() {

  }

  @Override
  public void onIconClicked() {

  }

  protected void showProgressMask() {

    if (mVProgressMask.getVisibility() == View.VISIBLE) return;

    mVProgressMask.setVisibility(View.VISIBLE);
    ObjectAnimator animator = ObjectAnimator.ofFloat(mVProgressMask, "alpha", 0f, 1f);
    animator.setDuration(150);
    animator.start();
  }

  protected void hideProgressMask() {
    if (mVProgressMask.getVisibility() == View.INVISIBLE) return;

    ObjectAnimator animator = ObjectAnimator.ofFloat(mVProgressMask, "alpha", 1f, 0f);
    animator.setDuration(150);
    animator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        onHideMaskStart();
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mVProgressMask.setVisibility(View.GONE);
        onHideMaskEnd();
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

  public final void showProgressBar(boolean mandatory) {
    showProgressBar();
    if (mandatory) {
      showProgressMask();
    }
  }

  protected void onHideMaskStart() {

  }

  protected void onHideMaskEnd() {

  }

  /**
   * @return true if fill window content
   */
  protected final boolean isFillContent() {
    return mFillContent;
  }

  protected final  void setFillContent(boolean fillContent) {
    if (mFillContent == fillContent) return;
    mFillContent = fillContent;
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mBaseView.findViewById(R.id.content).getLayoutParams();
    layoutParams.topMargin = fillContent ? 0 : getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);
    layoutParams.gravity = Gravity.TOP;
    mBaseView.requestLayout();
  }

  /**
   * Show a toast
   *
   * @param res        the string resource id
   * @param inActivity if true cancel the toast when activity finish.
   */
  protected void showToast(int res, boolean inActivity) {
    String string = getString(res);
    if (string != null) {
      showToast(string, inActivity);
    }
  }

  /**
   * @param res the string resource id
   * @see #showToast(int, boolean)
   */
  protected void showToast(int res) {
    showToast(res, true);
  }

  /**
   * Show a toast
   *
   * @param string     the string
   * @param inActivity if true cancel the toast when activity finish.
   */
  protected void showToast(String string, boolean inActivity) {
    if (mToast != null) {
      mToast.cancel();
    }

    if (mInActivityToast != null) {
      mInActivityToast.cancel();
    }

    if (inActivity) {
      mInActivityToast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
      mInActivityToast.show();
    } else {
      mToast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
      mToast.show();
    }
  }

  protected void showToast(String string) {
    showToast(string, true);
  }

  public final Handler getHandler() {
    return mHandler;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mBaseView = (ViewGroup) LayoutInflater.from(this)
        .inflate(R.layout.activity_base, (ViewGroup) findViewById(android.R.id.content), true);

    mTopBar = (TopBar) mBaseView.findViewById(R.id.top_bar);

    mProgressBar = (FrameLayout) mBaseView.findViewById(R.id.progress_bar);
    mVProgressMask = mBaseView.findViewById(R.id.v_progress_mask);
    mLlLoadingWrapper = (LinearLayout) mBaseView.findViewById(R.id.fl_loading_wrapper);
    mTvLoadingText = (TextView) mBaseView.findViewById(R.id.tv_loading);

    mTopBar.setCallback(this);

    mLlLoadingWrapper.setBackgroundDrawable(
        new RoundRectDrawable(dp2px(15), getResources().getColor(R.color.apptheme_progress_bar_bg)));

    Layout layout = getClass().getAnnotation(Layout.class);

    if (layout != null) {
      setContentView(layout.value());
    }

    mTopBar.getSearchEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
          onActionSearchClicked(v.getText().toString());
          return true;
        }
        return false;
      }
    });

    initAnimator();

    M.getRegisterHelper().register(this);

    if (shouldCheckUpgrade()) {
      checkUpgrade();
    }
  }

  @Override
  protected void onDestroy() {
    cancelAll();

    M.getRegisterHelper().unregister(this);

    hideProgressBar();

    super.onDestroy();

    android.util.Log.d("BaseActivity", "onDestroy");
  }

  @Override
  protected void onPause() {
    super.onPause();
    U.getAnalyser().onPause(this);

    if (mInActivityToast != null) mInActivityToast.cancel();

    sBackground = true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    U.getAnalyser().onResume(this);
    mResumed = true;

    sBackground = false;
  }

  protected final void hideTopBar(boolean animate) {
    if (!topBarShown()) return;
    ((FrameLayout.LayoutParams) findViewById(R.id.content).getLayoutParams()).topMargin = 0;
    mBaseView.requestLayout();
    if (animate) {
      if (mShowTopBarAnimator != null && mShowTopBarAnimator.isRunning()) {
        mShowTopBarAnimator.cancel();
      }
      mHideTopBarAnimator.start();
    } else {
      mTopBar.setVisibility(View.INVISIBLE);
    }
  }

  protected final void showTopBar(boolean animate) {
    if (topBarShown()) return;
    ((FrameLayout.LayoutParams) findViewById(R.id.content).getLayoutParams()).topMargin
        = mFillContent ? 0 : getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);
    mBaseView.requestLayout();
    if (animate) {
      if (mHideTopBarAnimator != null && mHideTopBarAnimator.isRunning()) {
        mHideTopBarAnimator.cancel();
      }
      mShowTopBarAnimator.start();
    } else {
      mTopBar.setVisibility(View.VISIBLE);
    }
  }

  public final void showRefreshIndicator() {
    mTopBar.mRefreshIndicator.show();
  }

  public final void showRefreshIndicator(boolean progressing) {
    mTopBar.mRefreshIndicator.show(progressing);
  }

  public final void hideRefreshIndicator() {
    mTopBar.mRefreshIndicator.hide();
  }

  protected final String getTopTitle() {
    return mTopBar.getTitle();
  }

  public final void setTopTitle(String title) {
    mTopBar.setTitle(title);
  }

  protected final String getTopSubTitle() {
    return mTopBar.getSubTitle();
  }

  public final void setTopSubTitle(String title) {
    mTopBar.setSubTitle(title);
  }

  public final TopBar getTopBar() {
    return mTopBar;
  }

  protected final void cacheIn(Object request, InputStream is) {
    RequestData data = new RequestData(request);
    new CacheInWorker(RESTRequester.genCacheKey(data.getApi(), data.getParams()), is).execute();
  }

  protected final void cacheIn(Object request, String string) {
    RequestData data = new RequestData(request);
    new CacheInWorker(RESTRequester.genCacheKey(data.getApi(), data.getParams()), string).execute();
  }


  protected final void cancelAll() {
  }

  protected final int dp2px(int dp) {
    return U.dp2px(dp);
  }

  protected final void setActionLeftDrawable(Drawable drawable) {
    mTopBar.getAbLeft().setDrawable(drawable);
  }

  protected final void setActionLeftVisibility(int visibility) {
    mTopBar.getAbLeft().setVisibility(visibility);
  }


  protected void onHandleMessage(Message message) {

  }


  protected void hideSoftKeyboard(View view) {
    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  protected void hideSoftKeyboard(View view, ResultReceiver resultReceiver) {
    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(view.getWindowToken(), 0, resultReceiver);
  }

  protected void showSoftKeyboard(View view) {
    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
        .showSoftInput(view, InputMethodManager.SHOW_FORCED);
  }

  private void initAnimator() {
    mHideTopBarAnimator = ObjectAnimator.ofFloat(mTopBar, "translationY", 0,
        -getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height));
    mHideTopBarAnimator.setDuration(150);
    mHideTopBarAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mTopBar.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    mShowTopBarAnimator = ObjectAnimator.ofFloat(mTopBar, "translationY",
        -getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height), 0f);
    mShowTopBarAnimator.setDuration(150);
    mShowTopBarAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        mTopBar.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
  }

  protected boolean shouldCheckUpgrade() {
    return true;
  }

  private long mLastCount;
  /**
   * 开始一个计时，清楚上次计时
   */
  protected void startCount() {
    mLastCount = System.currentTimeMillis();
  }

  /**
   * 获取当前计时
   * @return 计时，单位毫秒
   */
  protected long getCount() {
    return System.currentTimeMillis() - mLastCount;
  }

  @Override
  protected void onStop() {
    super.onStop();

    startCount();
  }

  private void checkUpgrade() {
    Sync sync = U.getSyncClient().getSync();
    if (sync != null && sync.upgrade != null) {
      int version;
      try {
        version = Integer.parseInt(sync.upgrade.version);
      } catch (NumberFormatException e) {
        version = -1;
      }
      if (version > C.VERSION) {
        if (sync.upgrade.force == 1) {
          new UpgradeDialog(this, sync.upgrade).show();
        } else {
          Calendar last = Calendar.getInstance();
          last.setTimeInMillis(Env.getUpgradeCanceledTimestamp());

          Calendar now = Calendar.getInstance();
          if (last.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR)) {
            new UpgradeDialog(this, sync.upgrade).show();
          }
        }
      }
    }
  }

  private boolean topBarShown() {
    return mTopBar.getVisibility() == View.VISIBLE;
  }
}
