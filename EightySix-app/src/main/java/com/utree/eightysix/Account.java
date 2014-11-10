package com.utree.eightysix;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.app.intro.IntroActivity;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.User;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.push.FetchAlarmReceiver;
import com.utree.eightysix.applogger.EntryAdapter;
import com.utree.eightysix.applogger.Payload;
import com.utree.eightysix.request.LogoutRequest;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class Account {

  private static Account sAccount = new Account();

  private String mUserId;
  private String mToken;

  private boolean mIsLogin;

  private Circle mCurrent;

  private Account() {
    mUserId = getSharedPreferences().getString("user_id", "");
    mToken = getSharedPreferences().getString("token", "");
    mIsLogin = !TextUtils.isEmpty(mUserId) && !TextUtils.isEmpty(mToken);

    M.getRegisterHelper().register(this);
  }

  public static Account inst() {
    return sAccount;
  }

  @Subscribe
  public void onCurrentCircleResponseEvent(CurrentCircleResponseEvent event) {
    mCurrent = event.getCircle();
  }

  public Circle getCurrentCircle() {
    return mCurrent;
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
    return getAccountSharedPreferences().getBoolean("settings_silent_mode", true);
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
      public LoginEvent() {
        M.getRegisterHelper().register(U.getPushHelper());
        U.getPushHelper().startWork();

        FetchAlarmReceiver.setupAlarm(U.getContext());

        //M.getRegisterHelper().register(ChatAccount.inst());
        //ChatAccount.inst().login();

        U.getAppLogger().log(new EntryAdapter() {

          private final Payload mPayload = new Payload();

          @Override
          public String getApi() {
            return "login";
          }

          @Override
          public Payload getPayload() {
            mPayload.put("user_id", Account.inst().getUserId());
            mPayload.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return mPayload;
          }
        });
      }
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

      // 停止拉消息服务
      U.getContext().stopService(new Intent(U.getContext(), FetchNotificationService.class));

      // 停止后台定时拉消息服务
      FetchAlarmReceiver.stopAlarm(U.getContext());

      if (!Account.inst().setUserId("") && !Account.inst().setToken("")) {
        Account.inst().mIsLogin = false;
      }

      U.getAppLogger().log(new EntryAdapter() {

        private final Payload mPayload = new Payload();

        @Override
        public String getApi() {
          return "logout";
        }

        @Override
        public Payload getPayload() {
          mPayload.put("user_id", Account.inst().getUserId());
          mPayload.put("timestamp", String.valueOf(System.currentTimeMillis()));
          return mPayload;
        }
      });
    }
  }

}
