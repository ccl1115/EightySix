package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 */
public class SquareFrameLayout extends FrameLayout {
    public SquareFrameLayout(Context context) {
        this(context, null, 0);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = widthMeasureSpec & ~(0x3 << 30);
        super.onMeasure(widthMeasureSpec, widthSize + MeasureSpec.EXACTLY);
    }
}
