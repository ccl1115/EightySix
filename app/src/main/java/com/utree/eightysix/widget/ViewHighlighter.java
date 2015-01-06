package com.utree.eightysix.widget;

import android.graphics.*;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class ViewHighlighter {

  private static final int MARGIN = U.dp2px(8);

  private View mTarget;
  private ViewGroup mParent;

  public ViewHighlighter(View view, ViewGroup parent) {
    mTarget = view;
    mParent = parent;
  }

  public Bitmap genMask() {
    if (mTarget == null || mParent == null) {
      return null;
    }

    Rect tRect = new Rect();
    Rect pRect = new Rect();

    mParent.getGlobalVisibleRect(pRect);
    mTarget.getGlobalVisibleRect(tRect);

    if (pRect.width() == 0 || pRect.height() == 0) {
      return null;
    }

    tRect.set(tRect.left - MARGIN, tRect.top - MARGIN, tRect.right + MARGIN, tRect.bottom + MARGIN);

    tRect.offset(-pRect.left, -pRect.top);
    pRect.offsetTo(0, 0);

    Paint src = new Paint();
    src.setColor(0x88000000);

    Paint dst = new Paint();
    dst.setColor(0x00000000);
    dst.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

    Bitmap bitmap = Bitmap.createBitmap(mParent.getMeasuredWidth(),
        mParent.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    canvas.drawRect(pRect, src);
    canvas.drawRect(tRect, dst);
    return bitmap;

  }
}
