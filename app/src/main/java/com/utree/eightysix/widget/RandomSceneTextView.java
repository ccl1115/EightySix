package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import java.util.Random;

/**
 * @author simon
 */
public class RandomSceneTextView extends LinearLayout {

  private TextView mTv;
  private TextView mSubTv;
  private ImageView mIv;

  public RandomSceneTextView(Context context) {
    this(context, null);
  }

  public RandomSceneTextView(Context context, AttributeSet attrs) {
    super(context, attrs);

    setOrientation(VERTICAL);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RandomSceneTextView, 0, 0);

    String text = ta.getString(R.styleable.RandomSceneTextView_text);
    String subText = ta.getString(R.styleable.RandomSceneTextView_subText);
    Drawable drawable = ta.getDrawable(R.styleable.RandomSceneTextView_drawable);

    mIv = new ImageView(context);
    mIv.setImageDrawable(drawable);

    mTv = new TextView(context);
    mTv.setTextSize(14);
    mTv.setTextColor(0xff888888);

    mTv.setText(text);

    mSubTv = new TextView(context);
    mSubTv.setTextSize(16);
    mSubTv.setTextColor(Color.BLACK);
    mSubTv.setText(subText);

    LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    params.gravity = Gravity.CENTER_HORIZONTAL;
    params.bottomMargin = U.dp2px(8);
    addView(mIv, params);

    params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    params.gravity = Gravity.CENTER_HORIZONTAL;
    params.bottomMargin = U.dp2px(8);
    addView(mTv, params);

    params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    params.gravity = Gravity.CENTER_HORIZONTAL;
    addView(mSubTv, params);
  }

  public void setText(int res) {
    setText(getResources().getString(res));
  }

  public void setSubText(int res) {
    setSubText(getResources().getString(res));
  }

  public void setText(CharSequence cs) {
    mTv.setText(cs);
  }

  public void setSubText(CharSequence cs) {
    mSubTv.setText(cs);
  }

  public void setDrawable(Drawable drawable) {
    mIv.setImageDrawable(drawable);
  }

  public void setDrawable(int res) {
    Drawable drawable = getResources().getDrawable(res);
    setDrawable(drawable);
  }
}
