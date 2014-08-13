package com.utree.eightysix.app.share;

import android.app.Activity;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
public class ShareManager {


  private IShare mShareToQQ = new ShareToQQ();
  private IShare mShareViaSMS = new ShareViaSMS();

  public ThemedDialog shareAppDialog(final Activity activity, final Circle circle) {
    return new ShareDialog(activity) {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new ShareAppViewHolder(activity, dialog, circle);
      }
    };
  }

  public ThemedDialog sharePostDialog(final Activity activity, final Circle circle, final Post post) {
    return new ShareDialog(activity) {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new SharePostViewHolder(activity, dialog, circle, post);
      }
    };
  }

  public ThemedDialog shareCommentDialog(final Activity activity, final Circle circle, final Post post, final String comment) {
    return new ShareDialog(activity) {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new ShareCommentViewHolder(activity, dialog, circle, post, comment);
      }
    };
  }

  @Keep
  public class ShareCommentViewHolder {
    private Activity mActivity;
    private ShareDialog mDialog;
    private Post mPost;
    private Circle mCircle;
    private String mComment;

    ShareCommentViewHolder(Activity activity, ShareDialog dialog, Circle circle, Post post, String comment) {
      mActivity = activity;
      mDialog = dialog;
      mPost = post;
      mComment = comment;
      mCircle = circle;
      ButterKnife.inject(this, dialog);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_msg");
      mShareViaSMS.shareComment(mActivity, mCircle, mPost, mComment);
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qq");
      mShareToQQ.shareComment(mActivity, mCircle, mPost, mComment);
      mDialog.dismiss();
    }
  }

  @Keep
  public class SharePostViewHolder {
    private Activity mActivity;
    private ShareDialog mDialog;
    private Post mPost;
    private Circle mCircle;

    SharePostViewHolder(Activity activity, ShareDialog dialog, Circle circle, Post post) {
      mActivity = activity;
      mDialog = dialog;
      mPost = post;
      mCircle = circle;
      ButterKnife.inject(this, dialog);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_msg");
      mShareViaSMS.sharePost(mActivity, mCircle, mPost);
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qq");
      mShareToQQ.sharePost(mActivity, mCircle, mPost);
      mDialog.dismiss();
    }
  }

  @Keep
  public class ShareAppViewHolder {
    private Activity mActivity;
    private ShareDialog mDialog;
    private Circle mCircle;

    ShareAppViewHolder(Activity activity, ShareDialog dialog, Circle circle) {
      mActivity = activity;
      mDialog = dialog;
      mCircle = circle;
      ButterKnife.inject(this, dialog);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_msg");
      mShareViaSMS.shareApp(mActivity, mCircle);
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qq");
      mShareToQQ.shareApp(mActivity, mCircle);
      mDialog.dismiss();
    }
  }
}
