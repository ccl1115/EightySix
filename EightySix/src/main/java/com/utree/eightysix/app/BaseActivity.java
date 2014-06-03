package com.utree.eightysix.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.aliyun.android.util.MD5Util;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.request.HandlerWrapper;
import com.utree.eightysix.request.RESTRequester;
import com.utree.eightysix.response.OnResponse;
import com.utree.eightysix.response.Response;
import com.utree.eightysix.widget.TopBar;
import de.akquinet.android.androlog.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides many base functionality to derived class
 *
 * <ul>
 *     <li>To show toast using {@link #showToast}</li>
 *     <li>To make api request using {@link #request}</li>
 *     <li>Auto bind views and OnClickListener to annotated fields using annotation
 *     {@link com.utree.eightysix.utils.ViewId} and {@link com.utree.eightysix.utils.OnClick}</li>
 *     <li>Automatically set content view when annotated with {@link com.utree.eightysix.app.Layout}</li>
 *     <li>An independent TopBar acts like the Android's ActionBar</li>
 *     <li>Auto register and unregister to Otto's event bus</li>
 *     <li>Automatically finish when LogoutEventFired, override onLogout() to prevent this</li>
 * </ul>
 */
public class BaseActivity extends Activity implements View.OnClickListener {

    private final Handler mHandler = new Handler();

    private Map<String, RequestHandle> mRequestHandles = new HashMap<String, RequestHandle>();

    private ViewGroup mBaseView;
    private TopBar mTopBar;

    private Toast mToast;

    private boolean mResumed;

    @Override
    public void onClick(View v) {
        Log.d(this, getResources().getResourceName(v.getId()));

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

    protected void showToast(int res) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, res, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected void showToast(String string) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseView = (ViewGroup) View.inflate(this, R.layout.activity_base, null);
        mTopBar = (TopBar) mBaseView.findViewById(R.id.top_bar);

        mTopBar.setOnActionOverflowClickListener(this);

        super.setContentView(mBaseView);

        Layout layout = getClass().getAnnotation(Layout.class);

        if (layout != null) {
            setContentView(layout.value());
        }

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
        mBaseView.addView(inflate,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1.0f)
        );

        U.viewBinding(findViewById(R.id.content), this);

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
        mBaseView.addView(contentView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT, 1.0f)
        );
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
        mTopBar.setVisibility(View.GONE);
    }

    protected final void showTopBar(boolean animate) {
        mTopBar.setVisibility(View.VISIBLE);
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
        mTopBar.showProgressBar();
    }

    protected final void hideProgressBar() {
        mTopBar.hideProgressBar();
    }

    protected final TopBar getTopBar() {
        return mTopBar;
    }

    protected final <T> void request(Object request, OnResponse<Response<T>> onResponse) {
        RESTRequester.RequestData data = U.getRESTRequester().convert(request);
        if (isRequesting(data.api, data.params)) return;

        RequestHandle handle = U.getRESTRequester().request(request,
                new HandlerWrapper<T>(genKey(data.api, data.params), onResponse));
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

    private boolean isRequesting(String api, RequestParams params) {
        RequestHandle executed = mRequestHandles.get(genKey(api, params));
        return executed != null && !executed.isCancelled() && !executed.isFinished();
    }

    private String genKey(String api, RequestParams params) {
        return MD5Util.getMD5String((api + params.toString()).getBytes()).toLowerCase();
    }

}
