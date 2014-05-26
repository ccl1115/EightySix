package com.utree.eightysix.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.aliyun.android.util.MD5Util;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.JsonHttpResponseHandler;
import com.utree.eightysix.widget.TopBar;
import de.akquinet.android.androlog.Log;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;

/**
 */
public class BaseActivity extends Activity implements View.OnClickListener {

    private final Handler mHandler = new Handler();

    private Map<String, RequestHandle> mRequestHandles = new HashMap<String, RequestHandle>();

    private ViewGroup mBaseView;
    private TopBar mTopBar;

    @Override
    public void onClick(View v) {
        Log.d(this, getResources().getResourceName(v.getId()));

        switch (v.getId()) {
            case R.id.top_bar_action_overflow:
                openOptionsMenu();
                break;
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        U.getAnalyser().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        U.getAnalyser().onPause(this);
    }

    @Override
    protected void onDestroy() {
        cancelAll();

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
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT, 1.0f)
        );

        U.viewBinding(findViewById(R.id.content), this);
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

    protected final void showProgressBar() {
        mTopBar.showProgressBar();
    }

    protected final void hideProgressBar() {
        mTopBar.hideProgressBar();
    }

    protected final TopBar getTopBar() {
        return mTopBar;
    }

    protected final <T> void get(String api, RequestParams params, JsonHttpResponseHandler<T> handler) {
        if (isRequesting(api)) return;

        try {
            RequestHandle handle = U.getRESTRequester().get(api, null, params,
                    new HandlerWrapper<T>(genCacheKey(api, params), handler));
            mRequestHandles.put(api, handle);
        } catch (Throwable t) {
            U.getAnalyser().reportException(this, t);
        }
    }

    protected final <T> void post(String api, RequestParams params, JsonHttpResponseHandler<T> handler) {
        if (isRequesting(api)) return;

        try {
            RequestHandle handle = U.getRESTRequester().post(api, null, params, null,
                    new HandlerWrapper<T>(genCacheKey(api, params), handler));
            mRequestHandles.put(api, handle);
        } catch (Throwable t) {
            U.getAnalyser().reportException(this, t);
        }
    }

    protected final void cancel(String api) {
        RequestHandle handle = mRequestHandles.get(api);
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

    private boolean isRequesting(String api) {
        RequestHandle executed = mRequestHandles.get(api);
        return executed != null && !executed.isCancelled() && !executed.isFinished();
    }

    private String genCacheKey(String api, RequestParams params) {
        return MD5Util.getMD5String((api + params.toString()).getBytes());
    }

    /**
     * Wrapper class use to cache response data automatically
     * @param <T> the type of json object
     */
    private static class HandlerWrapper<T> extends JsonHttpResponseHandler<T> {

        private String mKey;
        private JsonHttpResponseHandler<T> mHandler;

        public HandlerWrapper(String key, JsonHttpResponseHandler<T> handler) {
            mKey = key;
            mHandler = handler;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String rawResponse, T response) {
            if (rawResponse != null) {
                try {
                    U.getCache().edit(mKey).set(0, rawResponse);
                } catch (IOException e) {
                    U.getAnalyser().reportException(U.getContext(), e);
                }
            }

            mHandler.onSuccess(statusCode, headers, rawResponse, response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, T errorResponse) {
            mHandler.onFailure(statusCode, headers, e, rawData, errorResponse);
        }

        @Override
        public T parseResponse(String responseBody) throws Throwable {
            return mHandler.parseResponse(responseBody);
        }
    }
}
