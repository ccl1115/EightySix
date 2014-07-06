package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.utree.eightysix.U;
import java.util.Random;

/**
 * @author simon
 */
public class FontPortraitView extends TextView {

  private static Typeface sTypeface = Typeface.createFromAsset(U.getContext().getAssets(), "fonts/fontello.ttf");

  public FontPortraitView(Context context) {
    this(context, null, 0);
  }

  public FontPortraitView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FontPortraitView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setTypeface(sTypeface);
    setTextSize(30);
  }

  public void setEmotion(char c) {
    setText(String.valueOf(c));
  }

  public void setEmotionColor(int color) {
    setTextColor(color);
  }
}
