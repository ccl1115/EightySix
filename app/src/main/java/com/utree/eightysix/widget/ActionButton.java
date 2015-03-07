package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;

/**
 * @author simon
 */
public class ActionButton extends FrameLayout {

  private boolean mHasNew;

  private SparseBooleanArray mHasNews = new SparseBooleanArray();

  @InjectView(R.id.tv)
  public TextView mTv;

  @InjectView(R.id.iv)
  public ImageView mIv;

  @InjectView(R.id.cv)
  public CounterView mCv;

  @InjectView(R.id.rb_new)
  public RoundedButton mRbNew;

  public ActionButton(Context context) {
    this(context, null);
  }

  public ActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    LayoutInflater.from(context).inflate(R.layout.widget_action_button, this, true);

    ButterKnife.inject(this);
  }

  public void setActionLayoutParams(LayoutParams params) {}

  public void setActionBackgroundDrawable(Drawable drawable) {}

  public void setDrawable(Drawable drawable) {
    setBackgroundResource(R.drawable.apptheme_primary_btn_dark);
    if (drawable != null) {
      mIv.setVisibility(VISIBLE);
      mTv.setVisibility(GONE);
      mIv.setImageDrawable(drawable);
    } else {
      mIv.setVisibility(GONE);
    }
  }

  public void setText(CharSequence text) {
    setBackgroundResource(0);
    if (!TextUtils.isEmpty(text)) {
      mTv.setVisibility(VISIBLE);
      mTv.setText(text);
      mIv.setVisibility(GONE);
    } else {
      mTv.setVisibility(GONE);
    }
  }

  public void hide() {
    mTv.setVisibility(GONE);
    mIv.setVisibility(GONE);
    setOnClickListener(null);
  }

  public void setHasNew(boolean n) {
    mHasNew = n;
    if (mHasNew) {
      mRbNew.setVisibility(VISIBLE);
      mCv.setVisibility(GONE);
    } else {
      mRbNew.setVisibility(GONE);
    }
  }

  public void setHasNew(int index, boolean n) {
    mHasNews.append(index, n);

    for (int i = 0, size = mHasNews.size(); i < size; i++) {
      if (mHasNews.get(i, false)) {
        mHasNew = true;
      }
    }

    if (mHasNew) {
      mRbNew.setVisibility(VISIBLE);
      mCv.setVisibility(GONE);
    } else {
      mRbNew.setVisibility(GONE);
    }
  }

  public void setCount(int c) {
    if (c > 99) {
      c = 99;
    }
    mCv.setText(String.valueOf(c));
    if (c > 0) {
      mCv.setVisibility(VISIBLE);
      mRbNew.setVisibility(GONE);
    } else {
      mCv.setVisibility(GONE);
    }
  }
}
