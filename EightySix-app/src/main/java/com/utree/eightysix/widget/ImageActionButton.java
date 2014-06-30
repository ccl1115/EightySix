package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author simon
 */
public class ImageActionButton extends ActionButton {

  private ImageView mImageAction;

  public ImageActionButton(Context context) {
    this(context, null);
  }

  public ImageActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    mImageAction = new ImageView(context, attrs);
    LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mImageAction.setLayoutParams(lp);

    addView(mImageAction, 0);
  }

  public void setImageDrawable(Drawable drawable) {
    mImageAction.setImageDrawable(drawable);
  }

  public void setScaleType(ImageView.ScaleType scaleType) {
    mImageAction.setScaleType(scaleType);
  }
}
