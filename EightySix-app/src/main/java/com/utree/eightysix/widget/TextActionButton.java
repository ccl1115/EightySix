package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class TextActionButton extends ActionButton {

  private TextView mTextAction;

  public TextActionButton(Context context) {
    this(context, null);
  }

  public TextActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    mTextAction = new TextView(context, attrs);
    addView(mTextAction, 0);
    mTextAction.setDuplicateParentStateEnabled(true);
  }

  @Override
  public void setActionLayoutParams(LayoutParams params) {
    mTextAction.setLayoutParams(params);
  }

  @Override
  public void setActionBackgroundDrawable(Drawable drawable) {
    mTextAction.setBackgroundDrawable(drawable);
  }

  @Override
  public void setLayoutParams(ViewGroup.LayoutParams params) {
    mTextAction.setLayoutParams(new LayoutParams(params));
    super.setLayoutParams(params);
  }

  public void setGravity(int gravity) {
    mTextAction.setGravity(gravity);
  }

  public void setText(CharSequence cs) {
    mTextAction.setText(cs);
  }

  public void setText(int resId) {
    mTextAction.setText(resId);
  }

  public void setLines(int lines) {
    mTextAction.setLines(lines);
  }

  public void setActionPadding(int left, int top, int right, int bottom) {
    mTextAction.setPadding(left, top, right, bottom);
  }

  public void setSingleLine(boolean singleLine) {
    mTextAction.setSingleLine(singleLine);
  }

  public void setTextColor(int color) {
    mTextAction.setTextColor(color);
  }

  public void setTextSize(float size) {
    mTextAction.setTextSize(size);
  }
}
