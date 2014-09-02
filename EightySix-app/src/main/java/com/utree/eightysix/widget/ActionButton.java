package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;

/**
 * @author simon
 */
public class ActionButton extends FrameLayout {

  private boolean mHasNew;

  private Drawable mNewIndicator;

  private TextView mCountIndicator;

  private final int kIndicatorMargin;
  private final int kIndicatorSize;

  public ActionButton(Context context) {
    this(context, null);
  }

  public ActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    kIndicatorMargin = U.dp2px(8);
    kIndicatorSize = U.dp2px(8);

    mNewIndicator =
        new RoundRectDrawable(U.dp2px(10), getResources().getColor(R.color.apptheme_secondary_light_color));

    mCountIndicator = new TextView(context);
    mCountIndicator.setSingleLine(true);
    mCountIndicator.setTextSize(12);
    mCountIndicator.setTextColor(Color.WHITE);
    final int p = U.dp2px(5);
    mCountIndicator.setPadding(p, 0, p, 0);
    mCountIndicator.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(10),
        getResources().getColor(R.color.apptheme_secondary_light_color)));
    LayoutParams params =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int m = kIndicatorMargin;
    params.setMargins(m, m, m, m);
    params.gravity = Gravity.RIGHT;
    mCountIndicator.setLayoutParams(params);
    addView(mCountIndicator);
    mCountIndicator.setVisibility(GONE);

    mNewIndicator.setCallback(this);
  }

  public void setActionLayoutParams(LayoutParams params) {}

  public void setActionBackgroundDrawable(Drawable drawable) {}

  public void setHasNew(boolean n) {
    mHasNew = n;
    requestLayout();
    invalidate();
  }

  public void setCount(int c) {
    if (c > 99) {
      c = 99;
    }
    mCountIndicator.setText(String.valueOf(c));
    if (c > 0) {
      mCountIndicator.setVisibility(VISIBLE);
    } else {
      mCountIndicator.setVisibility(GONE);
    }
    requestLayout();
    invalidate();
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    mNewIndicator.setBounds(r - l - kIndicatorMargin - kIndicatorSize, t + kIndicatorMargin,
        r - l - kIndicatorMargin, t + kIndicatorMargin + kIndicatorSize);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    if (mHasNew) {
      mNewIndicator.draw(canvas);
    }
  }
}
