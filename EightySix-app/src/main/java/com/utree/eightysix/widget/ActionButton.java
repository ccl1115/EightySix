package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;

/**
 * @author simon
 */
public abstract class ActionButton extends FrameLayout {

  public static final int INDICATOR_MARGIN = 8;
  private boolean mHasNew;

  private Drawable mNewIndicator;

  private TextView mCountIndicator;

  public ActionButton(Context context) {
    this(context, null);
  }

  public ActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    mNewIndicator = getResources().getDrawable(R.drawable.apptheme_action_button_new_indicator);

    mCountIndicator = new TextView(context);
    mCountIndicator.setSingleLine(true);
    mCountIndicator.setTextSize(12);
    mCountIndicator.setTextColor(Color.WHITE);
    final int p = U.dp2px(4);
    mCountIndicator.setPadding(p, 0, p, 0);
    mCountIndicator.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(8),
        getResources().getColor(R.color.apptheme_secondary_light_color)));
    LayoutParams params =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int m = U.dp2px(8);
    params.setMargins(m, m, m, m);
    params.gravity = Gravity.RIGHT;
    mCountIndicator.setLayoutParams(params);
    addView(mCountIndicator);
    mCountIndicator.setVisibility(INVISIBLE);
  }

  public abstract void setActionLayoutParams(LayoutParams params);

  public abstract void setActionBackgroundDrawable(Drawable drawable);

  public void setHasNew(boolean n) {
    mHasNew = n;
    requestLayout();
    invalidate();
  }

  public void setCount(int c) {
    mCountIndicator.setText(String.valueOf(c));
    if (c > 0) {
      mCountIndicator.setVisibility(VISIBLE);
    } else {
      mCountIndicator.setVisibility(INVISIBLE);
    }
    requestLayout();
    invalidate();
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    final int m = U.dp2px(INDICATOR_MARGIN);
    mNewIndicator.setBounds(m - m - mNewIndicator.getIntrinsicWidth(), t + m, r - m, t + m + mNewIndicator.getIntrinsicHeight());
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    if (mHasNew) {
      mNewIndicator.draw(canvas);
    }
  }
}
