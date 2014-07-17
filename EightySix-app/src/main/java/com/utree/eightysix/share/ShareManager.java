package com.utree.eightysix.share;

import android.app.Activity;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.account.ContactsActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
public class ShareManager {


  private IShare mShareToQQ = new ShareToQQ();
  private IShare mShareViaSMS = new ShareViaSMS();

  public ThemedDialog shareAppDialog(final Activity activity, int factoryId) {
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

  public ThemedDialog sharePostDialog(final Activity activity, Post post) {
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

  public ThemedDialog shareCommentDialog(final Activity activity, Post post, String comment) {
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

  @Keep
  public class ShareCommentViewHolder {
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
      mShareViaSMS.shareComment(mActivity, mPost, mComment);
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      mShareToQQ.shareComment(mActivity, mPost, mComment);
    }
  }

  @Keep
  public class SharePostViewHolder {
    private Activity mActivity;
    private Post mPost;

    SharePostViewHolder(Activity activity, View view, Post post) {
      mActivity = activity;
      mPost = post;
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      mShareViaSMS.sharePost(mActivity, mPost);
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      mShareToQQ.sharePost(mActivity, mPost);
    }
  }

  @Keep
  public class ShareAppViewHolder {
    private Activity mActivity;
    private int mFactoryId;

    ShareAppViewHolder(Activity activity, View view, int factoryId) {
      mActivity = activity;
      mFactoryId = factoryId;
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      mShareViaSMS.shareApp(mActivity, mFactoryId);
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      mShareToQQ.shareApp(mActivity, mFactoryId);
    }
  }
}
