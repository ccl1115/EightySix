package com.utree.eightysix.qrcode.actions;

import android.net.Uri;
import com.utree.eightysix.U;
import com.utree.eightysix.qrcode.Action;
import com.utree.eightysix.request.AddFriendRequest;
import com.utree.eightysix.rest.*;
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
      Log.d(TAG, "path1 = " + paths.get(0));
      Log.d(TAG, "path2 = " + paths.get(1));
      if ("friend".equals(uri.getAuthority()) && paths.size() == 2 && "add".equals(paths.get(0))) {
        mId = paths.get(1);
        return true;
      }
    }
    return false;
  }

  @Override
  public void act(Uri uri) {
    RequestData data = U.getRESTRequester().convert(new AddFriendRequest(mId, 1));
    U.getRESTRequester().request(data, new HandlerWrapper<Response>(data, new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
        U.showToast("添加好友失败，请重新扫描");
      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          U.showToast("添加好友成功");
        } else {
          U.showToast("添加好友失败，请重新扫描");
        }
      }
    }, Response.class));
  }
}
