package com.utree.eightysix.qrcode.actions;

import android.net.Uri;
import com.utree.eightysix.qrcode.Action;

/**
 * @author simon
 */
public class AcceptRewardAction implements Action {
  private static final String TAG = "AcceptReward";

  @Override
  public boolean accept(Uri uri) {
    return "LANMEI_QRCODE".equals(uri.getScheme());
  }

  @Override
  public void act(Uri uri) {

  }
}
