package com.utree.eightysix.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.aliyun.android.util.MD5Util;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.M;
import com.utree.eightysix.drawable.GearsDrawable;
import com.utree.eightysix.utils.ImageUtils;
import static com.utree.eightysix.utils.ImageUtils.ImageLoadedEvent.FROM_DISK;
import static com.utree.eightysix.utils.ImageUtils.ImageLoadedEvent.FROM_MEM;
import static com.utree.eightysix.utils.ImageUtils.ImageLoadedEvent.FROM_REMOTE;

/**
 */
public class AsyncImageView extends ImageView {

  public static final String TAG = "AsyncImageView";

  private String mUrlHash;

  public AsyncImageView(Context context) {
    this(context, null, 0);
  }

  public AsyncImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    M.getRegisterHelper().register(this);
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (event.getHash().equals(mUrlHash)) {
      if (event.getBitmap() != null) {
        switch (event.getFrom()) {
          case FROM_MEM:
            break;
          case FROM_DISK:
            break;
          case FROM_REMOTE:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
              ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 255);
              valueAnimator.setDuration(500).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                  setImageAlpha((Integer) animation.getAnimatedValue());
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
        }
        setImageBitmap(event.getBitmap());
      }
    }
  }

  public void setUrl(String url) {
    if (url == null) {
      setImageBitmap(null);
      return;
    }

    mUrlHash = MD5Util.getMD5String(url.getBytes()).toLowerCase();

    setImageBitmap(null);

    clearAnimation();

    ImageUtils.asyncLoadWithRes(url, mUrlHash);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    M.getRegisterHelper().register(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    M.getRegisterHelper().unregister(this);
    super.onDetachedFromWindow();
  }

}
