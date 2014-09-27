package com.utree.eightysix.utils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

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
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
          public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (BuildConfig.DEBUG) {
              throwable.printStackTrace();
            }
            callback.onShorten(null);
          }
        });

  }
}
