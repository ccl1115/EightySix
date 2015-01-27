package com.utree.eightysix.app.share;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
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
  public void shareApp(final BaseActivity activity, final Circle circle, final String url) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected void onPreExecute() {
        activity.showProgressBar(true);
      }

      @Override
      protected Void doInBackground(Void... params) {
        Bundle data = new Bundle();
        data.putString(QzoneShare.SHARE_TO_QQ_TITLE, String.format(shareTitleForApp(), circle.shortName));
        data.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, String.format(shareContentForApp(), circle.shortName));
        data.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
        ArrayList<String> urls = new ArrayList<String>();
        urls.add("http://utree-resource.oss-cn-beijing.aliyuncs.com/faceless.png");
        data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
        data.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        shareToQzone(activity, data, defaultListener());
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        activity.hideProgressBar();
      }
    }.execute();
  }

  @Override
  public void sharePost(final BaseActivity activity, final Post post, final String url, final boolean fromBs) {
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected void onPreExecute() {
        activity.showProgressBar(true);
      }

      @Override
      protected Void doInBackground(Void... params) {
        Bundle data = new Bundle();
        data.putString(QzoneShare.SHARE_TO_QQ_TITLE, String.format(shareTitleForPost(), post.shortName));
        data.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
        data.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, post.content);
        if (!TextUtils.isEmpty(post.bgUrl) && post.bgUrl.contains(U.getImageBucket())) {
          ArrayList<String> urls = new ArrayList<String>();
          urls.add(post.bgUrl);
          data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
        } else {
          ArrayList<String> urls = new ArrayList<String>();
          urls.add("http://utree-resource.oss-cn-beijing.aliyuncs.com/faceless.png");
          data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
        }
        data.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        shareToQzone(activity, data, postUiCallback(post, fromBs));
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
        data.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitleForComment());
        data.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, String.format("\"%s\"", comment));
        data.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
        ArrayList<String> urls = new ArrayList<String>();
        if (!TextUtils.isEmpty(post.bgUrl) && post.bgUrl.contains(U.getImageBucket())) {
          urls.add(post.bgUrl);
          data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
        } else {
          urls.add("http://utree-resource.oss-cn-beijing.aliyuncs.com/faceless.png");
          data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
        }
        data.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        shareToQzone(activity, data, defaultListener());
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

  @Override
  public void shareBainian(final BaseActivity activity, final String recipient, final String content) {
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected void onPreExecute() {
        activity.showProgressBar(true);
      }

      @Override
      protected Void doInBackground(Void... params) {
        Bundle data = new Bundle();
        data.putString(QzoneShare.SHARE_TO_QQ_TITLE, String.format(shareTitleForBainian(), recipient));
        data.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);
        ArrayList<String> urls = new ArrayList<String>();
        urls.add("http://utree-resource.oss-cn-beijing.aliyuncs.com/faceless.png");
        data.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
        data.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        shareToQzone(activity, data, defaultListener());
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        activity.hideProgressBar();
      }
    }.execute();
  }

  @Override
  protected String shareTitleForBainian() {
    return "Hello %s，送你一张超炫酷的拜年卡，我是通过蓝莓制作的哦";
  }

  @Override
  protected String shareContentForBainian() {
    return null;
  }

}
