package com.utree.eightysix.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.widget.TopBar;
import de.akquinet.android.androlog.Log;

/**
 */
public class BaseActivity extends Activity implements View.OnClickListener {

    private final Handler mHandler = new Handler();

    private ViewGroup mBaseView;
    private TopBar mTopBar;

    protected final Handler getHandler() {
        return mHandler;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseView = (ViewGroup) View.inflate(this, R.layout.activity_base, null);
        mTopBar = (TopBar) mBaseView.findViewById(R.id.top_bar);

        mTopBar.setOnActionOverflowClickListener(this);

        super.setContentView(mBaseView);
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

    protected void hideTopBar(boolean animate) {
        mTopBar.setVisibility(View.GONE);
    }

    protected void showTopBar(boolean animate) {
        mTopBar.setVisibility(View.VISIBLE);
    }

    protected String getTopTitle() {
        return mTopBar.getTitle();
    }

    protected void setTopTitle(String title) {
        mTopBar.setTitle(title);
    }

    protected void showProgressBar() {
        mTopBar.showProgressBar();
    }

    protected void hideProgressBar() {
        mTopBar.hideProgressBar();
    }

    protected TopBar getTopBar() {
        return mTopBar;
    }

    @Override
    public void onClick(View v) {
        Log.d(this, getResources().getResourceName(v.getId()));

        switch (v.getId()) {
            case R.id.top_bar_action_overflow:
                openOptionsMenu();
                break;
        }
    }

}
