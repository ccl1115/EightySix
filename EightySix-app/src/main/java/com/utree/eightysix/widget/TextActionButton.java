package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author simon
 */
public class TextActionButton extends ActionButton {

  private TextView mActionView;

  public TextActionButton(Context context) {
    this(context, null);
  }

  public TextActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    mActionView = new TextView(context, attrs);
    LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mActionView.setLayoutParams(lp);

    addView(mActionView, 0);
  }


  public void setGravity(int gravity) {
    mActionView.setGravity(gravity);
  }

  public void setText(CharSequence cs) {
    mActionView.setText(cs);
  }

  public void setText(int resId) {
    mActionView.setText(resId);
  }

  public void setLines(int lines) {
    mActionView.setLines(lines);
  }

  public void setSingleLine(boolean singleLine) {
    mActionView.setSingleLine(singleLine);
  }

  public void setTextColor(int color) {
    mActionView.setTextColor(color);
  }

  public void setTextSize(float size) {
    mActionView.setTextSize(size);
  }
}
