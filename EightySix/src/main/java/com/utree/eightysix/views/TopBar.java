package com.utree.eightysix.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.utils.ViewMapping;

/**
 */
public class TopBar extends RelativeLayout {


    @ViewMapping.ViewId(R.id.top_bar_title)
    public TextView mTitle;

    @ViewMapping.ViewId(R.id.top_bar_progress)
    public ProgressBar mProgressBar;

    public TopBar(Context context) {
        this(context, null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.topBarStyle);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View.inflate(context, R.layout.widget_top_bar, this);

        ViewMapping.map(this, this);

        mProgressBar.setVisibility(GONE);
    }

    public void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    public String getTitle() {
        if (mTitle != null) {
            return mTitle.getText().toString();
        }
        return null;
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(GONE);
        }
    }
}
