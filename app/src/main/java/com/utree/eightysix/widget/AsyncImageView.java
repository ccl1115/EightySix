package com.utree.eightysix.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import static com.utree.eightysix.utils.ImageUtils.ImageLoadedEvent.FROM_DISK;
import static com.utree.eightysix.utils.ImageUtils.ImageLoadedEvent.FROM_MEM;
import static com.utree.eightysix.utils.ImageUtils.ImageLoadedEvent.FROM_REMOTE;
import java.io.File;

/**
 */
public class AsyncImageView extends ImageView {

  public static final String TAG = "AsyncImageView";

  private String mUrlHash;

  private boolean mLocal;

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
    if (event.getHash().equals(mUrlHash)
        && ((mLocal && (event.getWidth() == U.dp2px(48)) && (event.getHeight() == U.dp2px(48))) || !mLocal)
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
      setImageBitmap(event.getBitmap());
    }
  }

  public void setUrl(String url) {
    if (url == null) {
      setImageBitmap(null);
      return;
    }

    setImageBitmap(null);
    clearAnimation();

    if (url.startsWith("/")) {
      File file = new File(url);
      mUrlHash = IOUtils.fileHash(file);
      ImageUtils.asyncLoadThumbnail(file, mUrlHash);
      mLocal = true;
    } else {
      mUrlHash = ImageUtils.getUrlHash(url);
      ImageUtils.asyncLoadWithRes(url, mUrlHash);
      mLocal = false;
    }

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
