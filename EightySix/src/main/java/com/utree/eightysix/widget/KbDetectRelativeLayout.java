package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 */
public class KbDetectRelativeLayout extends RelativeLayout {

    private OnKeyboardVisibilityChanged mOnKeyboardVisibilityChanged;

    private int mLastMeasuredHeight;

    public KbDetectRelativeLayout(Context context) {
        this(context, null, 0);
    }

    public KbDetectRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KbDetectRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnKeyboardVisibilityChanged(OnKeyboardVisibilityChanged onKeyboardVisibilityChanged) {
        mOnKeyboardVisibilityChanged = onKeyboardVisibilityChanged;
    }

    public interface OnKeyboardVisibilityChanged {
        void onKeyboardVisibilityChanged(boolean visible);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mOnKeyboardVisibilityChanged != null) {
            final int measuredHeight = getMeasuredHeight();

            if (mLastMeasuredHeight > 0 && measuredHeight > 0) {
                if (measuredHeight > mLastMeasuredHeight) {
                    mOnKeyboardVisibilityChanged.onKeyboardVisibilityChanged(false);
                } else {
                    mOnKeyboardVisibilityChanged.onKeyboardVisibilityChanged(true);
                }
            }
            mLastMeasuredHeight = measuredHeight;
        }
    }
}
