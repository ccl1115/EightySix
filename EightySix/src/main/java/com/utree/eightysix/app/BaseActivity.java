package com.utree.eightysix.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.aliyun.android.util.MD5Util;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import static com.nineoldandroids.view.ViewHelper.getTranslationY;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.TopBar;
import de.akquinet.android.androlog.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides many base functionality to derived class
 * <p/>
 * <ul>
 * <li>To show toast using {@link #showToast}</li>
 * <li>To make api request using {@link #request}</li>
 * <li>Auto bind views and OnClickListener to annotated fields using annotation
 * {@link com.utree.eightysix.utils.ViewId} and {@link com.utree.eightysix.utils.OnClick}</li>
 * <li>Automatically set content view when annotated with {@link com.utree.eightysix.app.Layout}</li>
 * <li>An independent TopBar acts like the Android's ActionBar</li>
 * <li>Auto register and unregister to Otto's event bus</li>
 * <li>Automatically finish when LogoutEventFired, override onLogout() to prevent this</li>
 * </ul>
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener {

  private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      BaseActivity.this.onHandleMessage(msg);
    }
  };

  private Map<String, RequestHandle> mRequestHandles = new HashMap<String, RequestHandle>();

  private ViewGroup mBaseView;
  private TopBar mTopBar;
  private FrameLayout mProgressBar;

  private Toast mToast;

  private boolean mResumed;
  private ObjectAnimator mShowProgressBarAnimator;
  private ObjectAnimator mHideProgressBarAnimator;

  @Override
  public void onClick(View v) {
    Log.d("EightySix.OnClick", getResources().getResourceName(v.getId()));

    switch (v.getId()) {
      case R.id.top_bar_action_overflow:
        openOptionsMenu();
        break;
    }
  }

  /**
   * When LogoutEvent fired, finish all activities, except the login activity
   *
   * @param event the logout event
   */
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
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
   * @param res
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

    if (inActivity) {
      mToast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
      mToast.show();
    } else {
      Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
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

    mBaseView = (ViewGroup) View.inflate(this, R.layout.activity_base, null);
    super.setContentView(mBaseView);

    mTopBar = (TopBar) mBaseView.findViewById(R.id.top_bar);
    mTopBar.setOnActionOverflowClickListener(this);

    mProgressBar = (FrameLayout) mBaseView.findViewById(R.id.progress_bar);

    Layout layout = getClass().getAnnotation(Layout.class);

    if (layout != null) {
      setContentView(layout.value());
    }

    mTopBar.getSearchEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
          onSearchActionGo(v.getText().toString());
          return true;
        }
        return false;
      }
    });

    mTopBar.getSearchEditText().addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        onSearchTextChanged(s.toString());
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    U.getBus().register(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    U.getAnalyser().onResume(this);
    if (mResumed) {
      overridePendingTransition(R.anim.activity_exit_in, R.anim.activity_exit_out);
    } else {
      overridePendingTransition(R.anim.activity_enter_in, R.anim.activity_enter_out);
    }
    mResumed = true;
  }

  @Override
  protected void onPause() {
    super.onPause();
    U.getAnalyser().onPause(this);
  }

  @Override
  protected void onDestroy() {
    cancelAll();

    if (mToast != null) mToast.cancel();

    U.getBus().unregister(this);

    super.onDestroy();
  }

  @Override
  public final void setContentView(int layoutResID) {
    View content = mBaseView.findViewById(R.id.content);
    if (content != null) {
      mBaseView.removeView(content);
    }
    View inflate = View.inflate(this, layoutResID, null);
    inflate.setId(R.id.content);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    params.topMargin = getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);

    mBaseView.addView(inflate, 1, params);

    U.viewBinding(inflate, this);

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
    params.topMargin = getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);

    mBaseView.addView(contentView, 1, params);

    U.viewBinding(contentView, this);

    TopTitle topTitle = getClass().getAnnotation(TopTitle.class);

    if (topTitle != null) {
      setTopTitle(getString(topTitle.value()));
    }
  }

  @Override
  public final void setContentView(View contentView, ViewGroup.LayoutParams layoutParams) {
    View content = mBaseView.findViewById(R.id.content);
    if (content != null) {
      mBaseView.removeView(content);
    }
    contentView.setId(R.id.content);
    mBaseView.addView(contentView, layoutParams);
  }

  @Override
  public final void addContentView(View view, ViewGroup.LayoutParams params) {
    throw new RuntimeException("Call setContentView.");
  }

  protected final void hideTopBar(boolean animate) {
    ((RelativeLayout.LayoutParams) findViewById(R.id.content).getLayoutParams()).topMargin = 0;
    mBaseView.requestLayout();
    if (animate) {
      ObjectAnimator animator = ObjectAnimator.ofFloat(mTopBar, "translationY", 0, -mTopBar.getMeasuredHeight());
      animator.addListener(new Animator.AnimatorListener() {
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
      animator.setDuration(500);
      animator.start();
    } else {
      mTopBar.setVisibility(View.INVISIBLE);
    }
  }

  protected final void showTopBar(boolean animate) {
    ((RelativeLayout.LayoutParams) findViewById(R.id.content).getLayoutParams()).topMargin
        = getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height);
    mBaseView.requestLayout();
    mTopBar.setVisibility(View.VISIBLE);
    if (animate) {
      ObjectAnimator animator =
          ObjectAnimator.ofFloat(mTopBar, "translationY", -mTopBar.getMeasuredHeight(), 0f);
      animator.setDuration(500);
      animator.start();
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

  protected final void showProgressBar() {
    mProgressBar.setVisibility(View.VISIBLE);
    if (mShowProgressBarAnimator == null) {
      mShowProgressBarAnimator = ObjectAnimator.ofFloat(mProgressBar,
          "translationY",
          (getTranslationY(mProgressBar) == 0) ? mProgressBar.getMeasuredHeight() : getTranslationY(mProgressBar),
          0);
      mShowProgressBarAnimator.setDuration(500);
    }
    if (mHideProgressBarAnimator != null) mHideProgressBarAnimator.cancel();
    mShowProgressBarAnimator.start();
  }

  protected final void hideProgressBar() {
    if (mHideProgressBarAnimator == null) {
      mHideProgressBarAnimator = ObjectAnimator.ofFloat(mProgressBar,
          "translationY",
          getTranslationY(mProgressBar),
          mProgressBar.getMeasuredHeight());
      mHideProgressBarAnimator.setDuration(500);
      mHideProgressBarAnimator.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressBar.setVisibility(View.INVISIBLE);
          ViewHelper.setTranslationY(mProgressBar, 0);
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
  }

  protected final TopBar getTopBar() {
    return mTopBar;
  }

  protected final <T extends Response> void request(Object request, OnResponse<T> onResponse, Class<T> clz) {
    RESTRequester.RequestData data = U.getRESTRequester().convert(request);
    if (isRequesting(data.api, data.params)) return;

    RequestHandle handle = U.getRESTRequester().request(request,
        new HandlerWrapper<T>(genKey(data.api, data.params), request, onResponse, clz));
    mRequestHandles.put(data.api, handle);
  }

  protected final void cancel(String api, RequestParams params) {
    RequestHandle handle = mRequestHandles.get(genKey(api, params));
    if (handle != null) {
      handle.cancel(true);
      mRequestHandles.remove(api);
    }
  }

  protected final void cancelAll() {
    for (RequestHandle handle : mRequestHandles.values()) {
      handle.cancel(true);
    }
    mRequestHandles.clear();
  }

  protected final int dp2px(int dp) {
    return U.dp2px(dp);
  }

  protected void onHandleMessage(Message message) {

  }

  protected void onSearchActionGo(String keyword) {

  }

  protected void onSearchTextChanged(String newKeyword) {

  }

  private boolean isRequesting(String api, RequestParams params) {
    RequestHandle executed = mRequestHandles.get(genKey(api, params));
    return executed != null && !executed.isCancelled() && !executed.isFinished();
  }

  private String genKey(String api, RequestParams params) {
    return MD5Util.getMD5String((api + params.toString()).getBytes()).toLowerCase();
  }

}
