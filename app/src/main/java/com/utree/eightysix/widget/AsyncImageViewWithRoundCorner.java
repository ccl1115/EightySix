/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ImageUtils;

import static com.utree.eightysix.utils.ImageUtils.ImageLoadedEvent.*;

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
  }

  @Override
  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    int size = U.dp2px(48);
    Bitmap bitmap = event.getBitmap();
    if (event.getHash().equals(mUrlHash)
        && (mLocal && event.getWidth() == size && event.getHeight() == size || !mLocal)
        && (bitmap != null)) {
      switch (event.getFrom()) {
        case FROM_MEM:
          break;
        case FROM_DISK:
          break;
        case FROM_REMOTE:
          ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 255);
          valueAnimator.setDuration(500).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setImageAlpha((Integer) animation.getAnimatedValue());
              } else {
                setAlpha((Integer) animation.getAnimatedValue());
              }
            }
          });
          valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
          });
          valueAnimator.start();
          break;
      }
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
      setLayoutParams(new LinearLayout.LayoutParams(width, height));

      setImageDrawable(new RoundRectDrawable(U.dp2px(14), bitmap));
    }
  }

}
