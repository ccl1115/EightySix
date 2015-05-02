package com.utree.eightysix.qrcode.actions;

import android.net.Uri;
import com.utree.eightysix.U;
import com.utree.eightysix.app.friends.SendRequestActivity;
import com.utree.eightysix.qrcode.Action;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Pattern:  eightysix://friend/add/(\d+)
 *
 * @author simon
 */
public class AddFriendAction implements Action {

  public static final String TAG = "AddFriendAction";
  private String mId;

  /**
   * @param uri the uri to be parsed
   * @return true to consume this qrcode
   */
  @Override
  public boolean accept(Uri uri) {
    Log.d(TAG, "scheme = " + uri.getScheme());
    Log.d(TAG, "authority = " + uri.getAuthority());
    Log.d(TAG, "path = " + uri.getPath());
    Log.d(TAG, "host = " + uri.getHost());
    Log.d(TAG, "fragment = " + uri.getFragment());
    if ("eightysix".equals(uri.getScheme())) {
      List<String> paths = uri.getPathSegments();
      if ("friend".equals(uri.getAuthority()) && paths.size() == 2 && "add".equals(paths.get(0))) {
        mId = paths.get(1);
        return true;
      }
    }
    return false;
  }

  @Override
  public void act(Uri uri) {
    SendRequestActivity.start(U.getContext(), Integer.parseInt(mId));
  }
}
