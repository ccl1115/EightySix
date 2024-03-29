package com.utree.eightysix.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;

/**
 * @author simon
 */
public class ThemedDialog extends Dialog {

  @InjectView (R.id.rb_positive)
  public RoundedButton mRbPositive;

  @InjectView (R.id.rb_negative)
  public RoundedButton mRbNegative;

  @InjectView (R.id.fl_content)
  public FrameLayout mFlContent;

  @InjectView (R.id.tv_title)
  public TextView mTvTitle;

  @InjectView (R.id.v_top_divider)
  public View mVTopDivider;

  @InjectView (R.id.v_bot_divider)
  public View mVBotDivider;

  private boolean mShownPositive;
  private boolean mShownNegative;
  private View mBase;

  public ThemedDialog(Context context) {
    this(context, R.style.AppTheme_Dialog);
  }

  public ThemedDialog(Context context, int theme) {
    super(context, theme);

    mBase = LayoutInflater.from(getContext()).inflate(R.layout.dialog_base, null);
    mBase.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(5), Color.WHITE));
    ButterKnife.inject(this, mBase);
    mVTopDivider.setVisibility(View.INVISIBLE);
    mVBotDivider.setVisibility(View.INVISIBLE);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setCancelable(true);
    setCanceledOnTouchOutside(true);

    setContentView(mBase);
  }


  public void setContent(int layoutId) {
    LayoutInflater.from(getContext()).inflate(layoutId, mFlContent, true);
  }

  public void setContent(View view) {
    mFlContent.addView(view,
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
  }

  public void setPositive(int textId, View.OnClickListener listener) {
    setPositive(getContext().getString(textId), listener);
  }

  public void setPositive(CharSequence cs, View.OnClickListener listener) {
    mVBotDivider.setVisibility(View.VISIBLE);
    mShownPositive = true;
    mRbPositive.setVisibility(View.VISIBLE);

    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mRbPositive.getLayoutParams();
    if (mShownNegative) {
      layoutParams.leftMargin = U.dp2px(20);
      layoutParams.rightMargin = U.dp2px(10);

      ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mRbNegative.getLayoutParams();
      lp.leftMargin = U.dp2px(10);
      lp.rightMargin = U.dp2px(20);
      mRbNegative.setLayoutParams(lp);
    } else {
      layoutParams.leftMargin = U.dp2px(0);
      layoutParams.rightMargin = U.dp2px(0);
    }

    mRbPositive.setLayoutParams(layoutParams);
    mRbPositive.setText(cs);
    mRbPositive.setOnClickListener(listener);
  }

  public void setRbNegative(int textId, View.OnClickListener listener) {
    setRbNegative(getContext().getString(textId), listener);
  }

  public void setRbNegative(CharSequence cs, View.OnClickListener listener) {
    mVBotDivider.setVisibility(View.VISIBLE);
    mShownNegative = true;
    mRbNegative.setVisibility(View.VISIBLE);

    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mRbNegative.getLayoutParams();
    if (mShownPositive) {
      layoutParams.leftMargin = U.dp2px(10);
      layoutParams.rightMargin = U.dp2px(20);

      ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mRbPositive.getLayoutParams();
      lp.leftMargin = U.dp2px(20);
      lp.rightMargin = U.dp2px(10);
      mRbPositive.setLayoutParams(lp);
    } else {
      layoutParams.leftMargin = U.dp2px(0);
      layoutParams.rightMargin = U.dp2px(0);
    }

    mRbNegative.setLayoutParams(layoutParams);
    mRbNegative.setText(cs);
    mRbNegative.setOnClickListener(listener);
  }

  @Override
  public void setTitle(CharSequence title) {
    mTvTitle.setVisibility(View.VISIBLE);
    mVTopDivider.setVisibility(View.VISIBLE);
    mTvTitle.setText(title);
  }

  @Override
  public void setTitle(int titleId) {
    setTitle(getContext().getString(titleId));
  }
}
