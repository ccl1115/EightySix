package com.utree.eightysix.app.home;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;

/**
 */
public class HomeLayout extends FrameLayout {

  @InjectView(R.id.ib_send)
  public ImageView mIbSend;

  public HomeLayout(Context context) {
    this(context, null, 0);
  }

  public HomeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HomeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    ButterKnife.inject(this);

    mIbSend.setVisibility(INVISIBLE);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    drawChild(canvas, mIbSend, getDrawingTime());
  }
}
