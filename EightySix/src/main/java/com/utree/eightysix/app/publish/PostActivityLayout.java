package com.utree.eightysix.app.publish;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import butterknife.InjectView;

/**
 */
public class PostActivityLayout extends ViewGroup {

    @InjectView(R.id.fl_top)
    public FrameLayout mFlTop;

    @InjectView(R.id.fl_bottom)
    public FrameLayout mFlBottom;

    @InjectView(R.id.tv_bottom)
    public TextView mTvBottom;

    public PostActivityLayout(Context context) {
        this(context, null, 0);
    }

    public PostActivityLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PostActivityLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        inflate(context, R.layout.activity_post, this);
        U.viewBinding(this, this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = widthMeasureSpec & ~(0x3 << 30);
        final int heightSize = heightMeasureSpec & ~(0x3 << 30);

        int heightLeft = heightSize;

        if (mTvBottom.getVisibility() == GONE) {
            measureChild(mTvBottom, MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
        } else {
            measureChild(mTvBottom, widthMeasureSpec, heightSize + MeasureSpec.AT_MOST);
            heightLeft -= mTvBottom.getMeasuredHeight();
        }

        measureChild(mFlBottom, widthMeasureSpec, heightLeft + MeasureSpec.AT_MOST);
        heightLeft -= mFlBottom.getMeasuredHeight();
        measureChild(mFlTop, widthMeasureSpec, heightLeft + MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mFlTop.layout(l, 0, r, mFlTop.getMeasuredHeight());
        mFlBottom.layout(l, mFlTop.getBottom(), r, mFlTop.getBottom() + mFlBottom.getMeasuredHeight());
        mTvBottom.layout(l, mFlBottom.getBottom(), r, mFlBottom.getBottom() + mTvBottom.getMeasuredHeight());
    }
}
