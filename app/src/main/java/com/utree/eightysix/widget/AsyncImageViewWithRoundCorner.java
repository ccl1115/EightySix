/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;

import java.io.File;

/**
 */
public class AsyncImageViewWithRoundCorner extends AsyncImageView {

  private static final int IMAGE_MIN_WIDTH = 50;
  private static final int IMAGE_MAX_WIDTH = 200;

  private static final int IMAGE_MIN_HEIGHT = 50;
  private static final int IMAGE_MAX_HEIGHT = 300;

  private final int sImageMinWidth;
  private final int sImageMaxWidth;
  private final int sImageMinHeight;
  private final int sImageMaxHeight;

  private final Target mTarget = new Target() {
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
      setBitmap(bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable drawable) {

    }

    @Override
    public void onPrepareLoad(Drawable drawable) {

    }
  };

  private final int mRadius;

  private int mWidthMode;
  private int mHeightMode;

  public AsyncImageViewWithRoundCorner(Context context) {
    this(context, null);
  }

  public AsyncImageViewWithRoundCorner(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AsyncImageViewWithRoundCorner(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    sImageMinWidth = U.dp2px(IMAGE_MIN_WIDTH);
    sImageMaxWidth = U.dp2px(IMAGE_MAX_WIDTH);

    sImageMinHeight = U.dp2px(IMAGE_MIN_HEIGHT);
    sImageMaxHeight = U.dp2px(IMAGE_MAX_HEIGHT);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AsyncImageViewWithRoundCorner);

    mRadius = (int) ta.getDimension(R.styleable.AsyncImageViewWithRoundCorner_radius, U.dp2px(14));
  }

  @Override
  public void setUrl(String url) {
    super.setUrl(url);

    if (url == null) {
      setImageBitmap(null);
      return;
    }

    if (url.startsWith("/")) {
      File file = new File(url);
      Picasso.with(getContext()).load(file).resize(600, 600).into(mTarget);
    } else {
      Picasso.with(getContext()).load(url).resize(600, 600).into(mTarget);
    }
  }

  public void setUrl(String url, int width, int height) {
    if (url == null) {
      setImageBitmap(null);
      return;
    }

    if (url.startsWith("/")) {
      File file = new File(url);
      Picasso.with(getContext()).load(file).resize(width, height).into(mTarget);
    } else {
      Picasso.with(getContext()).load(url).resize(width, height).into(mTarget);
    }
  }

  private void setBitmap(Bitmap bitmap) {
    int width, height;
    if (bitmap.getWidth() < sImageMinWidth) {
      width = sImageMinWidth;
      height = Math.min(sImageMaxHeight,
          (int) (((float) width / bitmap.getWidth()) * bitmap.getHeight()));
    } else if (bitmap.getWidth() > sImageMaxWidth) {
      width = sImageMaxWidth;
      height = Math.max(sImageMinHeight,
          (int) (((float) width / bitmap.getWidth()) * bitmap.getHeight()));
    } else if (bitmap.getHeight() < sImageMinHeight) {
      height = sImageMinHeight;
      width = Math.max(sImageMaxWidth,
          (int) (((float) height / bitmap.getHeight()) * bitmap.getWidth()));
    } else if (bitmap.getHeight() > sImageMaxHeight) {
      height = sImageMaxHeight;
      width = Math.min(sImageMinWidth,
          (int) (((float) height / bitmap.getHeight()) * bitmap.getWidth()));
    } else {
      width = bitmap.getWidth();
      height = bitmap.getHeight();
    }
    if (mWidthMode == MeasureSpec.EXACTLY) {
      getLayoutParams().width = getMeasuredWidth();
    } else {
      getLayoutParams().width = width;
    }

    if (mHeightMode == MeasureSpec.EXACTLY) {
      getLayoutParams().height = getMeasuredHeight();
    } else {
      getLayoutParams().height = height;
    }

    setLayoutParams(getLayoutParams());

    setImageDrawable(new RoundRectDrawable(mRadius, bitmap));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    mWidthMode = widthMeasureSpec & (0x3 << 30);
    mHeightMode = heightMeasureSpec & (0x3 << 30);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}
