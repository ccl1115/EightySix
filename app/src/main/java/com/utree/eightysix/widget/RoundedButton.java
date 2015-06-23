package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.utree.eightysix.R;

/**
 */
public class RoundedButton extends TextView {

    private int mRadius;
    private ColorStateList mColorStateList;

    private final Paint mBackgroundPaint = new Paint();

    private final RectF mRect = new RectF();

    public RoundedButton(Context context) {
        this(context, null);
    }

    public RoundedButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.roundedButtonStyle);
    }

    public RoundedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton, defStyle, 0);

        mRadius = ta.getDimensionPixelOffset(R.styleable.RoundedButton_radius, 0);
      mColorStateList = ta.getColorStateList(R.styleable.RoundedButton_bgColor);

        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);

        if (mColorStateList != null) {
            mBackgroundPaint.setColor(mColorStateList.getColorForState(getDrawableState(), 0));
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        throw new IllegalStateException("Background can only set to color");
    }

    @Override
    public void setBackground(Drawable background) {
        throw new IllegalStateException("Background can only set to color");
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        throw new IllegalStateException("Background can only set to color");
    }

  public void setBackgroundColor(int color) {
    mColorStateList = null;
    mBackgroundPaint.setColor(color);
    invalidate();
  }

  public void setBackgroundColor(ColorStateList colorStateList) {
    mColorStateList = colorStateList;
    invalidate();
  }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mColorStateList != null) {
            int color = mColorStateList.getColorForState(getDrawableState(), 0);
            mBackgroundPaint.setColor(color);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mRect, mRadius, mRadius, mBackgroundPaint);
        super.onDraw(canvas);
    }
}
