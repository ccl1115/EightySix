package com.utree.eightysix.widget;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 */
public interface ITopBar2 {

  public static final int STYLE_TEXT = 1;
  public static final int STYLE_IMAGE = 2;

  public void setLeftText(String text);

  public void setLeftDrawable(Drawable drawable);

  public void hideLeft();

  public void setTitle(String text);

  public void setSubTitle(String text);

  public void setRightText(String text);

  public void hideRight();

  public void setRightDrawable(Drawable drawable);

  public void setCallback(Callback callback);

  public interface Callback {
    public void onLeftClicked(View v);

    public void onRightClicked(View v);
  }
}
