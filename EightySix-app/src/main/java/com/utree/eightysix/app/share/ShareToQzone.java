package com.utree.eightysix.app.share;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;

import java.util.ArrayList;

/**
 * @author simon
 */
class ShareToQzone extends IShare {

  private Tencent sTencent =
      Tencent.createInstance(U.getConfig("qq.app_id"), U.getContext().getApplicationContext());

  private void shareToQzone(Activity from, Bundle data, IUiListener listener) {
    data.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, U.getContext().getString(R.string.app_name));
    sTencent.shareToQzone(from, data, listener);
  }

  @Override
  public void shareApp(Activity activity, Circle circle) {
    Bundle data = new Bundle();
    data.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitleForApp());
    data.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContentForApp());
    data.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareLinkForApp(circle.id));
    data.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
    shareToQzone(activity, data, defaultListener());
  }

  @Override
  public void sharePost(Activity activity, Circle circle, Post post) {
    Bundle data = new Bundle();
    data.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitleForPost());
    data.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContentForPost());
    data.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareLinkForPost(post.id));
    ArrayList<String> urls = new ArrayList<String>();
    urls.add(post.bgUrl);
    data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
    data.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
    shareToQzone(activity, data, defaultListener());
  }

  @Override
  public void shareComment(Activity activity, Circle circle, Post post, String comment) {
    Bundle data = new Bundle();
    data.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitleForComment());
    data.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, comment);
    data.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareLinkForComment(post.id));
    ArrayList<String> urls = new ArrayList<String>();
    urls.add(post.bgUrl);
    data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
    data.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
    shareToQzone(activity, data, defaultListener());
  }

  private IUiListener defaultListener() {
    return new IUiListener() {
      @Override
      public void onComplete(Object o) {
        if (BuildConfig.DEBUG) Toast.makeText(U.getContext(), "onComplete", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onError(UiError uiError) {
        if (BuildConfig.DEBUG)
          Toast.makeText(U.getContext(),
              String.format("%d: %s - %s", uiError.errorCode, uiError.errorMessage, uiError.errorDetail),
              Toast.LENGTH_LONG).show();
      }

      @Override
      public void onCancel() {
        if (BuildConfig.DEBUG) Toast.makeText(U.getContext(), "onCancel", Toast.LENGTH_LONG).show();
      }
    };
  }
}