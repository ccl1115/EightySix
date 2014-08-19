package com.utree.eightysix.utils;

import android.os.Message;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.utree.eightysix.U;
import com.utree.eightysix.rest.RESTRequester;
import org.json.JSONObject;

/**
 * @author simon
 */
public class BdShortener implements Shortener {

  private static final String URL = "http://dwz.cn/create.php";

  @Override
  public void shorten(String url, final Callback callback) {
    U.getRESTRequester().getClient().post(U.getContext(), URL, new RequestParams("url", url),
        new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(JSONObject response) {
            if (response.optInt("status") == 0) {
              callback.onShorten(response.optString("tinyurl"));
            } else {
              callback.onShorten(null);
            }
          }

          @Override
          public void onFailure(Throwable e, JSONObject errorResponse) {
            callback.onShorten(null);
          }
        });
  }
}
