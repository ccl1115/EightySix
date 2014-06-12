package com.utree.eightysix.app.publish;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 */
public class PostActivityLayout extends ViewGroup {

    @InjectView(R.id.fl_top)
    public FrameLayout mFlTop;

    @InjectView(R.id.fl_panel)
    public FrameLayout mFlPanel;

    @InjectView(R.id.ll_bottom)
    public LinearLayout mLlBottom;

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

        if (mLlBottom.getVisibility() == GONE) {
            measureChild(mLlBottom, MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
        } else {
            measureChild(mLlBottom, widthMeasureSpec, heightSize + MeasureSpec.AT_MOST);
            heightLeft -= mLlBottom.getMeasuredHeight();
        }

        measureChild(mFlPanel, widthMeasureSpec, heightLeft + MeasureSpec.AT_MOST);
        heightLeft -= mFlPanel.getMeasuredHeight();
        measureChild(mFlTop, widthMeasureSpec, heightLeft + MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mFlTop.layout(l, 0, r, mFlTop.getMeasuredHeight());
        mFlPanel.layout(l, mFlTop.getBottom(), r, mFlTop.getBottom() + mFlPanel.getMeasuredHeight());
        mLlBottom.layout(l, mFlPanel.getBottom(), r, mFlPanel.getBottom() + mLlBottom.getMeasuredHeight());
    }
}
