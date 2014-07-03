package com.utree.eightysix.utils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class ShareUtils {

  private static Tencent sTencent =
      Tencent.createInstance(U.getConfig("qq.app_id"), U.getContext().getApplicationContext());

  public static void shareToQQ(Activity from, Bundle data, IUiListener listener) {
    data.putString(QQShare.SHARE_TO_QQ_APP_NAME, U.getContext().getString(R.string.app_name));
    sTencent.shareToQQ(from, data, listener);
  }

  public static void shareAppToQQ(final Activity activity) {
    Bundle data = new Bundle();
    data.putString(QQShare.SHARE_TO_QQ_TITLE, "分享蓝莓应用");
    data.putString(QQShare.SHARE_TO_QQ_SUMMARY, "匿名的蓝领社交圈");
    data.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.baidu.com");
    data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
    shareToQQ(activity, data, new IUiListener() {
      @Override
      public void onComplete(Object o) {
        if (BuildConfig.DEBUG) Toast.makeText(activity, "onComplete", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onError(UiError uiError) {
        if (BuildConfig.DEBUG)
          Toast.makeText(activity,
              String.format("%d: %s - %s", uiError.errorCode, uiError.errorMessage, uiError.errorDetail),
              Toast.LENGTH_LONG).show();
      }

      @Override
      public void onCancel() {
        if (BuildConfig.DEBUG) Toast.makeText(activity, "onCancel", Toast.LENGTH_LONG).show();
      }
    });
  }

  public static void sharePostToQQ(final Activity activity, Post post) {
    Bundle data = new Bundle();
    data.putString(QQShare.SHARE_TO_QQ_TITLE, "分享个秘密");
    data.putString(QQShare.SHARE_TO_QQ_SUMMARY, post.content);
    data.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.baidu.com");
    data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
    shareToQQ(activity, data, new IUiListener() {
      @Override
      public void onComplete(Object o) {
        if (BuildConfig.DEBUG) Toast.makeText(activity, "onComplete", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onError(UiError uiError) {
        if (BuildConfig.DEBUG)
          Toast.makeText(activity,
              String.format("%d: %s - %s", uiError.errorCode, uiError.errorMessage, uiError.errorDetail),
              Toast.LENGTH_LONG).show();
      }

      @Override
      public void onCancel() {
        if (BuildConfig.DEBUG) Toast.makeText(activity, "onCancel", Toast.LENGTH_LONG).show();
      }
    });
  }
}
