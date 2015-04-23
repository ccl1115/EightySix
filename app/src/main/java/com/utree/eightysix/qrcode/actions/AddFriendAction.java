package com.utree.eightysix.qrcode.actions;

import android.net.Uri;
import com.utree.eightysix.U;
import com.utree.eightysix.qrcode.Action;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
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
    U.request("user_friend_request", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          U.showToast("添加好友成功");
        }
      }
    }, Response.class, mId, "来自扫一扫");
  }
}
