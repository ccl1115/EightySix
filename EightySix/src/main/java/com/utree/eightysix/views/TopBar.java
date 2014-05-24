package com.utree.eightysix.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.ViewBinding;

/**
 */
public class TopBar extends ViewGroup implements View.OnClickListener {

    @ViewBinding.ViewId(R.id.top_bar_title)
    public TextView mTitle;

    @ViewBinding.ViewId(R.id.top_bar_progress)
    public ProgressBar mProgressBar;

    @ViewBinding.ViewId(R.id.top_bar_action_overflow)
    @ViewBinding.OnClick
    public ImageView mActionOverFlow;
    public OnClickListener mOnActionOverflowClickListener;

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

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar, defStyle, 0);

        mTitle.setTextColor(ta.getColor(R.styleable.TopBar_titleColor, Color.GRAY));

        mActionOverFlow.setBackgroundDrawable(ta.getDrawable(R.styleable.TopBar_actionBgSelector));
        mActionOverFlow.setOnClickListener(this);

        mProgressBar.setVisibility(GONE);
    }

    public String getTitle() {
        if (mTitle != null) {
            return mTitle.getText().toString();
        }
        return null;
    }

    public void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
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

    public void setOnActionOverflowClickListener(OnClickListener onClickListener) {
        mOnActionOverflowClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.top_bar_action_overflow:
                if (mOnActionOverflowClickListener != null) {
                    mOnActionOverflowClickListener.onClick(v);
                }
                break;
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int heightMode = heightMeasureSpec & (0x3 << 30);
        final int heightSize = heightMeasureSpec & ~(0x3 << 30);

        final int widthMode = widthMeasureSpec & (0x3 << 30);
        final int widthSize = widthMeasureSpec & ~(0x3 << 30);

        int widthLeft = widthSize;

        // keep it as a square
        measureChild(mActionOverFlow, heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

        widthLeft -= mActionOverFlow.getMeasuredWidth();

        measureChild(mProgressBar, heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

        widthLeft -= mProgressBar.getMeasuredWidth();

        measureChild(mTitle, widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int right = r;
        final int height = b - t;

        mTitle.layout(0, (height - mTitle.getMeasuredHeight()) >> 1,
                mTitle.getMeasuredWidth(), (height + mTitle.getMeasuredHeight()) >> 1);

        mActionOverFlow.layout(right - mActionOverFlow.getMeasuredWidth(), 0, right, b);

        right -= mActionOverFlow.getMeasuredWidth();

        mProgressBar.layout(right - mProgressBar.getMeasuredWidth(), 0, right, b);
    }

}
