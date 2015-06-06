package com.utree.eightysix.qrcode.actions;

import android.net.Uri;
import com.utree.eightysix.U;
import com.utree.eightysix.app.friends.SendRequestActivity;
import com.utree.eightysix.qrcode.Action;

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
