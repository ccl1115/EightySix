package com.utree.eightysix;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import com.google.gson.reflect.TypeToken;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.app.intro.IntroActivity;
import com.utree.eightysix.data.User;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.request.LogoutRequest;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;
import de.akquinet.android.androlog.Log;
import java.util.List;

/**
 */
public class Account {

  private static Account sAccount = new Account();

  private String mUserId;
  private String mToken;

  private boolean mIsLogin;

  private Account() {
    mUserId = U.getContext().getSharedPreferences("account", Context.MODE_PRIVATE).getString("user_id", "");
    mToken = U.getContext().getSharedPreferences("account", Context.MODE_PRIVATE).getString("token", "");
    mIsLogin = !TextUtils.isEmpty(mUserId) && !TextUtils.isEmpty(mToken);
  }

  public static Account inst() {
    return sAccount;
  }

  public String getToken() {
    return mToken;
  }

  public String getUserId() {
    return mUserId;
  }

  public void login(String userId, String token) {
    if (setUserId(userId) && setToken(token)) {
      mIsLogin = true;
      U.getBus().post(new LoginEvent());
    }
  }

  public void login(User user) {
    if (setUserId(user.userId) && setToken(user.token)) {
      mIsLogin = true;
      U.getBus().post(new LoginEvent());
    }
  }

  public void logout() {
    U.getBus().post(new LogoutEvent());
    if (!setUserId("") && !setToken("")) {
      mIsLogin = false;
    }
  }

  public boolean isLogin() {
    return mIsLogin;
  }

  public List<String> getSearchHistory() {
    String json = getAccountSharedPreferences().getString("search_history", "[]");

    return U.getGson().fromJson(json, new TypeToken<List<String>>() {
    }.getType());
  }

  public void setSearchHistory(List<String> history) {
    getAccountSharedPreferences().edit().putString("search_history", U.getGson().toJson(history)).apply();
  }

  public void setHasNewPraise(boolean has) {
    U.getBus().post(new HasNewPraiseEvent(has));
    getAccountSharedPreferences().edit().putBoolean("new_praise", has).apply();
  }

  public boolean getHasNewPraise() {
    return getAccountSharedPreferences().getBoolean("new_praise", false);
  }

  public void setNewCommentCount(int count) {
    int value = Math.max(count, 0);
    U.getBus().post(new NewCommentCountEvent(value));
    getAccountSharedPreferences().edit().putInt("new_comment_count", value).apply();
  }

  public void incNewCommentCount(int count) {
    int value = getNewCommentCount() + count;
    U.getBus().post(new NewCommentCountEvent(value));
    getAccountSharedPreferences().edit().putInt("new_comment_count", value).apply();
  }

  public void decNewCommentCount(int count) {
    int value = Math.max(getNewCommentCount() - count, 0);
    U.getBus().post(new NewCommentCountEvent(value));
    getAccountSharedPreferences().edit().putInt("new_comment_count", value).apply();
  }

  public int getNewCommentCount() {
    return getAccountSharedPreferences().getInt("new_comment_count", 0);
  }

  public void setSilentMode(boolean toggle) {
    getAccountSharedPreferences().edit().putBoolean("settings_silent_mode", toggle).apply();
  }

  public boolean getSilentMode() {
    return getAccountSharedPreferences().getBoolean("settings_silent_mode", false);
  }

  private boolean setToken(String token) {
    mToken = token;

    SharedPreferences preferences = getSharedPreferences();
    if (TextUtils.isEmpty(mToken)) {
      preferences.edit().putString("token", "").apply();
      return false;
    } else {
      preferences.edit().putString("token", mToken).apply();
      return true;
    }
  }

  private boolean setUserId(String userId) {
    mUserId = userId;

    SharedPreferences preferences = getSharedPreferences();
    if (TextUtils.isEmpty(mUserId)) {
      preferences.edit().putString("user_id", "").apply();
      return false;
    } else {
      preferences.edit().putString("user_id", mUserId).apply();
      return true;
    }
  }

  private SharedPreferences getSharedPreferences() {
    return U.getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
  }

  private SharedPreferences getAccountSharedPreferences() {
    return U.getContext().getSharedPreferences("account_" + mUserId, Context.MODE_PRIVATE);
  }

  public static class LoginEvent {

  }

  public static class LogoutEvent {

    /**
     * When fire this event, start the login activity.
     */
    public LogoutEvent() {
      RequestData data = U.getRESTRequester().convert(new LogoutRequest());
      U.getRESTRequester().request(data, new HandlerWrapper<Response>(data, new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          // We don't care about the response
        }
      }, Response.class));

      Intent intent = new Intent(U.getContext(), IntroActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      U.getContext().startActivity(intent);

      ((NotificationManager) U.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
  }

}
