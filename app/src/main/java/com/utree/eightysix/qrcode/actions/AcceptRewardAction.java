package com.utree.eightysix.qrcode.actions;

import android.net.Uri;
import com.utree.eightysix.qrcode.Action;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class AcceptRewardAction implements Action {
  private static final String TAG = "AcceptReward";

  @Override
  public boolean accept(Uri uri) {
    Log.d(TAG, "scheme = " + uri.getScheme());
    Log.d(TAG, "authority = " + uri.getAuthority());
    Log.d(TAG, "path = " + uri.getPath());
    Log.d(TAG, "host = " + uri.getHost());
    Log.d(TAG, "fragment = " + uri.getFragment());
    return "LANMEI_QRCODE".equals(uri.getScheme());
  }

  @Override
  public void act(Uri uri) {

  }
}
