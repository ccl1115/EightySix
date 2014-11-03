package com.utree.eightysix.app.share;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
class ShareToQQ extends IShare {

  private Tencent sTencent =
      Tencent.createInstance(BuildConfig.DEBUG ? U.getConfig("qq.app_id") : U.getConfig("qq.app_id.release"), U.getContext().getApplicationContext());

  private void shareToQQ(Activity from, Bundle data, IUiListener listener) {
    data.putString(QQShare.SHARE_TO_QQ_APP_NAME, U.getContext().getString(R.string.app_name));
    sTencent.shareToQQ(from, data, listener);
  }

  @Override
  public void shareApp(final BaseActivity activity, final Circle circle, final String url) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected void onPreExecute() {
        activity.showProgressBar(true);
      }

      @Override
      protected Void doInBackground(Void... params) {
        Bundle data = new Bundle();
        data.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitleForApp());
        data.putString(QQShare.SHARE_TO_QQ_SUMMARY, String.format(shareContentForApp(), circle.shortName));
        data.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        data.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://utree-resource.oss-cn-beijing.aliyuncs.com/faceless.png");
        data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        shareToQQ(activity, data, defaultListener());
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        activity.hideProgressBar();
      }
    }.execute();
  }

  @Override
  public void sharePost(final BaseActivity activity, final Post post, final String url) {
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected void onPreExecute() {
        activity.showProgressBar(true);
      }

      @Override
      protected Void doInBackground(Void... params) {
        Bundle data = new Bundle();
        data.putString(QQShare.SHARE_TO_QQ_TITLE, String.format(shareTitleForPost(), post.shortName));
        data.putString(QQShare.SHARE_TO_QQ_SUMMARY, String.format(shareContentForPost(), post.shortName));
        data.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        if (!TextUtils.isEmpty(post.bgUrl) && post.bgUrl.contains(U.getImageBucket())) {
          data.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, post.bgUrl);
        } else {
          data.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://utree-resource.oss-cn-beijing.aliyuncs.com/faceless.png");
        }
        data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        shareToQQ(activity, data, defaultListener());
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        activity.hideProgressBar();
      }
    }.execute();
  }

  @Override
  public void shareComment(final BaseActivity activity, final Post post, final String comment, final String url) {
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected void onPreExecute() {
        activity.showProgressBar(true);
      }

      @Override
      protected Void doInBackground(Void... params) {
        Bundle data = new Bundle();
        data.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitleForComment());
        data.putString(QQShare.SHARE_TO_QQ_SUMMARY, comment);
        data.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        if (!TextUtils.isEmpty(post.bgUrl) && post.bgUrl.contains(U.getImageBucket())) {
          data.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, post.bgUrl);
        } else {
          data.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://utree-resource.oss-cn-beijing.aliyuncs.com/faceless.png");
        }
        data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        shareToQQ(activity, data, defaultListener());
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        activity.hideProgressBar();
      }
    }.execute();
  }

  @Override
  public void shareTag(BaseActivity activity, Circle circle, int tagId, String url) {
    shareApp(activity, circle, url);
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
