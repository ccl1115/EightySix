package com.utree.eightysix.utils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

/**
 * @author simon
 */
public class WeiboShortener implements Shortener {

  private static final String API = "https://api.weibo.com/2/short_url/shorten.json";

  @Override
  public void shorten(final String url, final Callback callback) {
    U.getRESTRequester().getClient().get(U.getContext(), API, new RequestParams("source", "1681459862", "url_long", url),
        new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(JSONObject response) {
            JSONArray urls = response.optJSONArray("urls");
            if (urls != null && urls.length() == 1) {
              JSONObject url = urls.optJSONObject(0);
              if (url != null && "true".equals(url.optString("result"))) {
                callback.onShorten(url.optString("url_short"));
                return;
              }
            }
            callback.onShorten(null);
          }

          @Override
          public void onFailure(Throwable e, JSONObject errorResponse) {
            if (BuildConfig.DEBUG) {
              e.printStackTrace();
            }
            callback.onShorten(null);
          }
        });

  }
}
