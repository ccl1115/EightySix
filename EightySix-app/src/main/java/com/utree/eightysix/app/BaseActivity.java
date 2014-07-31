package com.utree.eightysix.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import static com.nineoldandroids.view.ViewHelper.getTranslationY;
import com.utree.eightysix.C;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.settings.UpgradeDialog;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.event.LogoutListener;
import com.utree.eightysix.rest.CacheInWorker;
import com.utree.eightysix.rest.CacheOutWorker;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.TopBar;
import java.io.InputStream;
import java.util.Calendar;

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

  private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      BaseActivity.this.onHandleMessage(msg);
    }
  };
  public View mVProgressMask;
  private FrameLayout mProgressBar;
  private LinearLayout mLlLoadingWrapper;
  private TextView mTvLoadingText;
  private ViewGroup mBaseView;
  private TopBar mTopBar;
  private ObjectAnimator mHideTopBarAnimator;
  private ObjectAnimator mShowTopBarAnimator;
  private AnimatorSet mShowProgressBarAnimator;
  private AnimatorSet mHideProgressBarAnimator;

  private Toast mToast;
  private Toast mInActivityToast;

  private boolean mResumed;
  private boolean mFillContent;

  @Override
  public final void setContentView(int layoutResID) {
    View content = mBaseView.findViewById(R.id.content);
    if (content != null) {
      mBaseView.removeView(content);
    }
    View inflate = LayoutInflater.from(this).inflate(layoutResID, mBaseView, false);
    inflate.setId(R.id.content);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    params.topMargin = mFillContent ? 0 : getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);

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
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    params.topMargin = mFillContent ? 0 : getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);

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
    mTvLoadingText.setText(res);
  }

  public void setLoadingText(String text) {
    mTvLoadingText.setText(text);
  }

  public final <T extends Response> void request(Object request, OnResponse<T> onResponse, Class<T> clz) {
    RequestData data = U.getRESTRequester().convert(request);

    U.getRESTRequester().request(request, new HandlerWrapper<T>(data, onResponse, clz));
  }

  public final <T extends Response> void cacheOut(Object request, OnResponse<T> onResponse, Class<T> clz) {
    RequestData data = U.getRESTRequester().convert(request);

    new CacheOutWorker<T>(RESTRequester.genCacheKey(data.getApi(), data.getParams()), onResponse, clz).execute();
  }

  @Override
  public void onActionLeftClicked() {

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
        mVProgressMask.setVisibility(View.INVISIBLE);
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

  protected final void showProgressBar(boolean mandatory) {
    showProgressBar();
    if (mandatory) {
      showProgressMask();
    }
  }

  protected void onHideMaskStart() {

  }

  protected void onHideMaskEnd() {

  }

  protected boolean isFillContent() {
    return mFillContent;
  }

  public void setFillContent(boolean fillContent) {
    if (mFillContent == fillContent) return;
    mFillContent = fillContent;
    ((RelativeLayout.LayoutParams) mBaseView.findViewById(R.id.content).getLayoutParams()).topMargin =
        fillContent ? 0 : getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);
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

  protected final Handler getHandler() {
    return mHandler;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mBaseView = (ViewGroup) LayoutInflater.from(this)
        .inflate(R.layout.activity_base, (ViewGroup) findViewById(android.R.id.content), false);

    super.setContentView(mBaseView);

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
  }

  @Override
  protected void onPause() {
    super.onPause();
    U.getAnalyser().onPause(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    U.getAnalyser().onResume(this);
    //if (mResumed) {
    //  overridePendingTransition(R.anim.activity_exit_in, R.anim.activity_exit_out);
    //} else {
    //  overridePendingTransition(R.anim.activity_enter_in, R.anim.activity_enter_out);
    //}
    mResumed = true;

    if (mInActivityToast != null) mInActivityToast.cancel();
  }

  protected final void hideTopBar(boolean animate) {
    if (!topBarShown()) return;
    ((RelativeLayout.LayoutParams) findViewById(R.id.content).getLayoutParams()).topMargin = 0;
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
    ((RelativeLayout.LayoutParams) findViewById(R.id.content).getLayoutParams()).topMargin
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

  protected final String getTopTitle() {
    return mTopBar.getTitle();
  }

  protected final void setTopTitle(String title) {
    mTopBar.setTitle(title);
  }

  protected final String getTopSubTitle() {
    return mTopBar.getSubTitle();
  }

  protected final void setTopSubTitle(String title) {
    mTopBar.setSubTitle(title);
  }

  protected final TopBar getTopBar() {
    return mTopBar;
  }

  protected final void cacheIn(Object request, InputStream is) {
    RequestData data = U.getRESTRequester().convert(request);
    new CacheInWorker(RESTRequester.genCacheKey(data.getApi(), data.getParams()), is).execute();
  }

  protected final void cacheIn(Object request, String string) {
    RequestData data = U.getRESTRequester().convert(request);
    new CacheInWorker(RESTRequester.genCacheKey(data.getApi(), data.getParams()), string).execute();
  }


  protected final void cancelAll() {
  }

  protected final int dp2px(int dp) {
    return U.dp2px(dp);
  }

  protected final void setActionLeftDrawable(Drawable drawable) {
    if (drawable == null) {
      mTopBar.mActionLeft.setPadding(U.dp2px(10), 0, 0, 0);
    } else {
      mTopBar.mActionLeft.setPadding(0, 0, 0, 0);
    }
    mTopBar.mActionLeft.setImageDrawable(drawable);
  }

  protected final void setActionLeftVisibility(int visibility) {
    mTopBar.mActionLeft.setVisibility(visibility);
  }


  protected void onHandleMessage(Message message) {

  }


  protected void hideSoftKeyboard(View view) {
    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(view.getWindowToken(), 0);
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
