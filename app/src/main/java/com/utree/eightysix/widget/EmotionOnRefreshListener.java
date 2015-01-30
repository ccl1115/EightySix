package com.utree.eightysix.widget;


import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.TextView;
import com.utree.eightysix.U;

import java.util.Random;

/**
 * @author simon
 */
public abstract class EmotionOnRefreshListener extends Handler implements IRefreshable.OnRefreshListener {
  private static final int MSG_ANIMATE = 0x1;

  private TextView mHead;
  private Random mRandom = new Random();

  public EmotionOnRefreshListener(TextView head) {
    mHead = head;
    mHead.setTypeface(Typeface.createFromAsset(U.getContext().getAssets(), "fonts/refresh_icon.ttf"));
  }

  @Override
  public void onStateChanged(IRefreshable.State state) {
    switch (state) {
      case pulling_refresh:
        mHead.setText(String.format("%c", (char) (0xe801 + mRandom.nextInt(14))));
        break;

    }
  }

  @Override
  public void onPreRefresh() {
    sendEmptyMessageDelayed(MSG_ANIMATE, 500);
  }

  @Override
  public void onRefreshUI() {
    removeMessages(MSG_ANIMATE);
  }

  @Override
  public void handleMessage(Message msg) {
    if (msg.what == MSG_ANIMATE) {
      mHead.setText(String.format("%c", (char) (0xe801 + mRandom.nextInt(14))));
      ((ViewGroup) mHead.getParent()).invalidate();
      sendEmptyMessageDelayed(MSG_ANIMATE, 500);
    }
  }
}
