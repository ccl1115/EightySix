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

    super.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    mImageAction = new ImageView(context, attrs);
    LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mImageAction.setLayoutParams(lp);

    addView(mImageAction, 0);

    mImageAction.setDuplicateParentStateEnabled(true);
  }

  @Override
  public void setActionLayoutParams(LayoutParams params) {
    mImageAction.setLayoutParams(params);
  }

  @Override
  public void setActionBackgroundDrawable(Drawable drawable) {
    mImageAction.setBackgroundDrawable(drawable);
  }

  public void setImageDrawable(Drawable drawable) {
    mImageAction.setImageDrawable(drawable);
  }

  public void setScaleType(ImageView.ScaleType scaleType) {
    mImageAction.setScaleType(scaleType);
  }


  @Override
  public void setLayoutParams(ViewGroup.LayoutParams params) {
    mImageAction.setLayoutParams(new LayoutParams(params));
    removeView(mImageAction);
    addView(mImageAction, 0);
    invalidate();
  }

}
