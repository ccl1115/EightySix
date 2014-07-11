package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;

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
    setTextSize(20);
  }

  @Override
  public void setTextColor(int color) {
    super.setTextColor(color);
    setBackgroundDrawable(new RoundRectDrawable(U.dp2px(15),
        Color.argb(0xff,
            Math.min(0xff, Color.red(color) + 0x88),
            Math.min(0xff, Color.green(color) + 0x88),
            Math.min(0xff, Color.blue(color) + 0x88))));
  }

  public void setEmotion(char c) {
    setText(String.valueOf(c));
  }

  public void setEmotionColor(int color) {
    setTextColor(color);
  }
}
