package com.utree.eightysix.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.aliyun.android.util.MD5Util;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.request.RESTRequester;
import com.utree.eightysix.response.Response;
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
    private float mDensity;

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

        mDensity = getResources().getDisplayMetrics().density;

        mBaseView = (ViewGroup) View.inflate(this, R.layout.activity_base, null);
        mTopBar = (TopBar) mBaseView.findViewById(R.id.top_bar);

        mTopBar.setOnActionOverflowClickListener(this);

        super.setContentView(mBaseView);

        Layout layout = getClass().getAnnotation(Layout.class);

        if (layout != null) {
            setContentView(layout.value());
        }
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

    protected final void showProgressBar() {
        mTopBar.showProgressBar();
    }

    protected final void hideProgressBar() {
        mTopBar.hideProgressBar();
    }

    protected final TopBar getTopBar() {
        return mTopBar;
    }

    protected final <T> void request(Object request, Response<T> response, Class<T> tClass) {
        RESTRequester.RequestData data = U.getRESTRequester().convert(request);
        if (isRequesting(data.api)) return;

        RequestHandle handle = U.getRESTRequester().request(request,
                new HandlerWrapper<T>(genCacheKey(data.api, data.params), response, tClass));
        mRequestHandles.put(data.api, handle);
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

    protected final int dp2px(int dp) {
        return (int) (mDensity * dp + 0.5f);
    }

    private boolean isRequesting(String api) {
        RequestHandle executed = mRequestHandles.get(api);
        return executed != null && !executed.isCancelled() && !executed.isFinished();
    }

    private String genCacheKey(String api, RequestParams params) {
        return MD5Util.getMD5String((api + params.toString()).getBytes()).toLowerCase();
    }

    /**
     * Wrapper class use to cache response data automatically
     *
     * @param <T> the type of json object
     */
    private static class HandlerWrapper<T> extends JsonHttpResponseHandler<T> {

        private String mKey;
        private Response<T> mResponse;
        private Class<T> mTClass;

        public HandlerWrapper(String key, Response<T> response, Class<T> tClass) {
            mKey = key;
            mResponse = response;
            mTClass = tClass;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String rawResponse, T response) {
            Log.d(C.TAG.RR, "response: " + rawResponse);
            if (response != null) {
                try {
                    U.getApiCache().edit(mKey).set(0, rawResponse);
                } catch (IOException e) {
                    U.getAnalyser().reportException(U.getContext(), e);
                }
                mResponse.onResponse(response);
            } else {
                mResponse.onResponse(null);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, T errorResponse) {
            mResponse.onResponse(null);
        }

        @Override
        public T parseResponse(String responseBody) throws Throwable {
            return U.getGson().fromJson(responseBody, mTClass);
        }
    }
}
