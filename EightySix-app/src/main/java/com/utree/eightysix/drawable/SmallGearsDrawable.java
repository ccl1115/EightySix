package com.utree.eightysix.drawable;

import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class SmallGearsDrawable extends GearsDrawable {

  @Override
  protected void setDrawables() {
    mGear5 = (android.graphics.drawable.BitmapDrawable) U.gd(R.drawable.gear5_s);
    mGear6 = (android.graphics.drawable.BitmapDrawable) U.gd(R.drawable.gear6_s);
    mGear8 = (android.graphics.drawable.BitmapDrawable) U.gd(R.drawable.gear8_s);
  }

  @Override
  public int getIntrinsicHeight() {
    return U.dp2px(64);
  }

  @Override
  public int getIntrinsicWidth() {
    return U.dp2px(64);
  }
}
