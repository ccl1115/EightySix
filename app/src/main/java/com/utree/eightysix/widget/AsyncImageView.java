package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 */
public class AsyncImageView extends ImageView {

  public static final String TAG = "AsyncImageView";

  protected String mUrlHash;

  public AsyncImageView(Context context) {
    this(context, null, 0);
  }

  public AsyncImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setUrl(String url) {
    setImageBitmap(null);

    if (url == null || url.length() == 0) {
      return;
    }

    if (url.startsWith("/")) {
      File file = new File(url);
      Picasso.with(getContext()).load(file).into(this);
    } else {
      Picasso.with(getContext()).load(url).into(this);
    }

  }

  public void setUrl(String url, int width, int height) {
    setImageBitmap(null);

    if (url == null || url.length() == 0) {
      return;
    }

    if (url.startsWith("/")) {
      File file = new File(url);
      Picasso.with(getContext()).load(file).resize(width, height).into(this);
    } else {
      Picasso.with(getContext()).load(url).resize(width, height).into(this);
    }
  }

}
