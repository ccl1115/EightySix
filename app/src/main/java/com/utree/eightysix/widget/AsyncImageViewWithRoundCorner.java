/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.widget;

import android.content.Context;
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

  public AsyncImageViewWithRoundCorner(Context context) {
    this(context, null);
  }

  public AsyncImageViewWithRoundCorner(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AsyncImageViewWithRoundCorner(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    int size = U.dp2px(48);
    if (event.getHash().equals(mUrlHash)
        && (mLocal && event.getWidth() == size && event.getHeight() == size || !mLocal)
        && (event.getBitmap() != null)) {
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
      setImageDrawable(new RoundRectDrawable(U.dp2px(14), event.getBitmap()));
      setLayoutParams(new LinearLayout.LayoutParams(event.getBitmap().getWidth(),
          event.getBitmap().getHeight()));
    }
  }

}
