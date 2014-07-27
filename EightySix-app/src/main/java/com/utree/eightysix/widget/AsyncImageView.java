package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.aliyun.android.util.MD5Util;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.ImageUtils;

/**
 */
public class AsyncImageView extends ImageView {

  public static final String TAG = "AsyncImageView";

  private String mUrlHash;

  private String mUrl;

  public AsyncImageView(Context context) {
    this(context, null, 0);
  }

  public AsyncImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    U.getBus().register(this);
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (event.getHash().equals(mUrlHash)) {
      if (event.getBitmap() != null) {
        setImageBitmap(event.getBitmap());
      }
    }
  }

  public void setUrl(String url) {
    if (url == null) {
      setImageBitmap(null);
      mUrl = null;
      return;
    }

    //if (url.equals(mUrl)) return;

    mUrl = url;
    mUrlHash = MD5Util.getMD5String(url.getBytes()).toLowerCase();

    setImageBitmap(null);

    ImageUtils.asyncLoad(url, mUrlHash);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    U.getBus().unregister(this);
  }
}
