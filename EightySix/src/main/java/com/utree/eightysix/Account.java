package com.utree.eightysix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.response.UserResponse;
import com.utree.eightysix.response.data.User;
import de.akquinet.android.androlog.Log;

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
        Log.v("Account", " userId = " + mUserId);
        Log.v("Account", "  token = " + mToken);
        Log.v("Account", "isLogin = " + mIsLogin);
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
        if (!setUserId("") && !setToken("")) {
            mIsLogin = false;
            U.getBus().post(new LogoutEvent());
        }
    }

    public boolean isLogin() {
        return mIsLogin;
    }

    private boolean setToken(String token) {
        mToken = token;

        SharedPreferences preferences = U.getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
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

        SharedPreferences preferences = U.getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(mUserId)) {
            preferences.edit().putString("user_id", "").apply();
            return false;
        } else {
            preferences.edit().putString("user_id", mUserId).apply();
            return true;
        }
    }

    public static class LoginEvent {

    }

    public static class LogoutEvent {

        /**
         * When fire this event, start the login activity.
         */
        public LogoutEvent() {
            U.getContext().startActivity(new Intent(U.getContext(), LoginActivity.class));
        }
    }

}
