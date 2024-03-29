package com.utree.eightysix.app;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.widget.guide.Component;
import com.utree.eightysix.widget.guide.Guide;
import com.utree.eightysix.widget.guide.GuideBuilder;

/**
 * @author simon
 */
public class OverlayTipUtil {

  public static Guide getPortraitTip(View target, final View.OnClickListener listener) {
    return new GuideBuilder()
        .setAlpha(0x88)
        .setAutoDismiss(false)
        .setTargetView(target)
        .setOverlayTarget(true)
        .addComponent(new Component() {
          @Override
          public View getView(LayoutInflater inflater) {
            View view = inflater.inflate(R.layout.overlay_tip_portrait, null, false);
            view.findViewById(R.id.ll_tip).setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4), Color.WHITE));
            view.setOnClickListener(listener);
            return view;
          }

          @Override
          public int getAnchor() {
            return ANCHOR_TOP;
          }

          @Override
          public int getFitPosition() {
            return FIT_START;
          }

          @Override
          public int getXOffset() {
            return -16;
          }

          @Override
          public int getYOffset() {
            return -4;
          }
        }).createGuide();
  }

}
