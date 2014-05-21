package com.utree.eightysix.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.ViewBinding;

/**
 */
public class TopBar extends RelativeLayout {


    @ViewBinding.ViewId(R.id.top_bar_title)
    public TextView mTitle;

    @ViewBinding.ViewId(R.id.top_bar_progress)
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
        U.viewBinding(this, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

        mTitle.setTextColor(ta.getColor(R.styleable.TopBar_titleColor, 0xFFFFFFFF));

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
