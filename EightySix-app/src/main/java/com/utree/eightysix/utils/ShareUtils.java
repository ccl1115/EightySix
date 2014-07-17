package com.utree.eightysix.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.account.ContactsActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.ThemedDialog;

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

  public static void shareAppToQQ(final Activity activity, int factoryId) {
    Bundle data = new Bundle();
    data.putString(QQShare.SHARE_TO_QQ_TITLE, "分享蓝莓圈");
    data.putString(QQShare.SHARE_TO_QQ_SUMMARY, "匿名的蓝领社交圈");
    data.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
        String.format("%s/shareapp.do?userId=%s&factoryId=%d", U.getConfig("api.host"), Account.inst().getUserId(), factoryId));
    data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
    shareToQQ(activity, data, defaultListener());
  }

  public static void sharePostToQQ(final Activity activity, Post post) {
    Bundle data = new Bundle();
    // TODO 文案
    data.putString(QQShare.SHARE_TO_QQ_TITLE, "来自蓝莓圈的帖子");
    data.putString(QQShare.SHARE_TO_QQ_SUMMARY, post.content);
    data.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
        String.format("%s/sharecontent.do?userId=%s&postVirtualId=%s",
            U.getConfig("api.host"), Account.inst().getUserId(), post.id));
    data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
    shareToQQ(activity, data, defaultListener());
  }

  public static void shareCommentToQQ(final Activity activity, Post post, String comment) {
    Bundle data = new Bundle();
    data.putString(QQShare.SHARE_TO_QQ_TITLE, "来自蓝莓圈的评论");
    data.putString(QQShare.SHARE_TO_QQ_SUMMARY, comment);
    data.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
        String.format("%s/sharecontent.do?userId=%s&postVirtualId=%s",
            U.getConfig("api.host"), Account.inst().getUserId(), post.id));
    data.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
    shareToQQ(activity, data, defaultListener());
  }

  public static ThemedDialog shareAppDialog(final Activity activity, int factoryId) {
    ThemedDialog dialog;
    dialog = new ThemedDialog(activity);
    dialog.setTitle("分享给厂里的朋友");
    dialog.setCanceledOnTouchOutside(true);
    View view = activity.getLayoutInflater().inflate(R.layout.dialog_content_share, null, false);
    dialog.setContent(view);
    new ShareAppViewHolder(activity, view, factoryId);
    dialog.show();
    return dialog;
  }

  public static ThemedDialog sharePostDialog(final Activity activity, Post post) {
    ThemedDialog dialog;
    dialog = new ThemedDialog(activity);
    dialog.setTitle("分享给厂里的朋友");
    dialog.setCanceledOnTouchOutside(true);
    View view = activity.getLayoutInflater().inflate(R.layout.dialog_content_share, null, false);
    dialog.setContent(view);
    new SharePostViewHolder(activity, view, post);
    dialog.show();
    return dialog;
  }

  public static ThemedDialog shareCommentDialog(final Activity activity, Post post, String comment) {
    ThemedDialog dialog;
    dialog = new ThemedDialog(activity);
    dialog.setTitle("分享给厂里的朋友");
    dialog.setCanceledOnTouchOutside(true);
    View view = activity.getLayoutInflater().inflate(R.layout.dialog_content_share, null, false);
    dialog.setContent(view);
    new ShareCommentViewHolder(activity, view, post, comment);
    dialog.show();
    return dialog;
  }

  private static IUiListener defaultListener() {
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

  @Keep
  public static class ShareCommentViewHolder {
    private Activity mActivity;
    private Post mPost;
    private String mComment;

    ShareCommentViewHolder(Activity activity, View view, Post post, String comment) {
      mActivity = activity;
      mPost = post;
      mComment = comment;
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      ContactsActivity.start(mActivity, "comment");
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      ShareUtils.shareCommentToQQ(mActivity, mPost, mComment);
    }
  }

  @Keep
  public static class SharePostViewHolder {
    private Activity mActivity;
    private Post mPost;

    SharePostViewHolder(Activity activity, View view, Post post) {
      mActivity = activity;
      mPost = post;
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      ContactsActivity.start(mActivity, "comment");
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      ShareUtils.sharePostToQQ(mActivity, mPost);
    }
  }

  @Keep
  public static class ShareAppViewHolder {
    private Activity mActivity;
    private int mFactoryId;

    ShareAppViewHolder(Activity activity, View view, int factoryId) {
      mActivity = activity;
      mFactoryId = factoryId;
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      ContactsActivity.start(mActivity, "comment");
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      ShareUtils.shareAppToQQ(mActivity, mFactoryId);
    }
  }
}
