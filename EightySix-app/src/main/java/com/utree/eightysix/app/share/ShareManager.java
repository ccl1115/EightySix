package com.utree.eightysix.app.share;

import android.app.Activity;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
public class ShareManager {


  private IShare mShareToQQ = new ShareToQQ();
  private IShare mShareViaSMS = new ShareViaSMS();

  public ThemedDialog shareAppDialog(final Activity activity, final int factoryId) {
    return new ShareDialog(activity) {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new ShareAppViewHolder(activity, dialog, factoryId);
      }
    };
  }

  public ThemedDialog sharePostDialog(final Activity activity, final Post post) {
    return new ShareDialog(activity) {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new SharePostViewHolder(activity, dialog, post);
      }
    };
  }

  public ThemedDialog shareCommentDialog(final Activity activity, final Post post, final String comment) {
    return new ShareDialog(activity) {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new ShareCommentViewHolder(activity, dialog, post, comment);
      }
    };
  }

  @Keep
  public class ShareCommentViewHolder {
    private Activity mActivity;
    private ShareDialog mDialog;
    private Post mPost;
    private String mComment;

    ShareCommentViewHolder(Activity activity, ShareDialog dialog, Post post, String comment) {
      mActivity = activity;
      mDialog = dialog;
      mPost = post;
      mComment = comment;
      ButterKnife.inject(this, dialog);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      mShareViaSMS.shareComment(mActivity, mPost, mComment);
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      mShareToQQ.shareComment(mActivity, mPost, mComment);
      mDialog.dismiss();
    }
  }

  @Keep
  public class SharePostViewHolder {
    private Activity mActivity;
    private ShareDialog mDialog;
    private Post mPost;

    SharePostViewHolder(Activity activity, ShareDialog dialog, Post post) {
      mActivity = activity;
      mDialog = dialog;
      mPost = post;
      ButterKnife.inject(this, dialog);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      mShareViaSMS.sharePost(mActivity, mPost);
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      mShareToQQ.sharePost(mActivity, mPost);
      mDialog.dismiss();
    }
  }

  @Keep
  public class ShareAppViewHolder {
    private Activity mActivity;
    private ShareDialog mDialog;
    private int mFactoryId;

    ShareAppViewHolder(Activity activity, ShareDialog dialog, int factoryId) {
      mActivity = activity;
      mDialog = dialog;
      mFactoryId = factoryId;
      ButterKnife.inject(this, dialog);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      mShareViaSMS.shareApp(mActivity, mFactoryId);
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      mShareToQQ.shareApp(mActivity, mFactoryId);
      mDialog.dismiss();
    }
  }
}
