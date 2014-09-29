package com.utree.eightysix.app.share;

import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.Shortener;
import com.utree.eightysix.utils.WeiboShortener;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
public class ShareManager {


  private IShare mShareToQQ = new ShareToQQ();
  private IShare mShareToQzone = new ShareToQzone();
  private IShare mShareViaSMS = new ShareViaSMS();

  private Shortener mShortener = new WeiboShortener();

  public ThemedDialog shareAppDialog(final BaseActivity activity, final Circle circle) {
    return new ShareDialog(activity, "邀请厂里的朋友") {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new ShareAppViewHolder(activity, dialog, circle);
      }
    };
  }

  public ThemedDialog sharePostDialog(final BaseActivity activity, final Post post) {
    return new ShareDialog(activity, "转发内容至") {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new SharePostViewHolder(activity, dialog, post);
      }
    };
  }

  public void shareAppToQzone(final BaseActivity activity, final Circle circle) {
    mShortener.shorten(shareLinkForApp(circle.id), new Shortener.Callback() {
      @Override
      public void onShorten(String shorten) {
        if (shorten != null) {
          mShareToQzone.shareApp(activity, circle, shorten);
        } else {
          mShareToQzone.shareApp(activity, circle, shareLinkForApp(circle.id));
        }
      }
    });
  }

  public void shareAppToQQ(final BaseActivity activity, final Circle circle) {
    mShortener.shorten(shareLinkForApp(circle.id), new Shortener.Callback() {
      @Override
      public void onShorten(String shorten) {
        if (shorten != null) {
          mShareToQQ.shareApp(activity, circle, shorten);
        } else {
          mShareToQQ.shareApp(activity, circle, shareLinkForApp(circle.id));
        }
      }
    });
  }

  public ThemedDialog shareCommentDialog(final BaseActivity activity, final Post post, final String comment) {
    return new ShareDialog(activity, "转发内容至") {
      @Override
      protected Object getViewHolder(ShareDialog dialog) {
        return new ShareCommentViewHolder(activity, dialog, post, comment);
      }
    };
  }

  static String shareLinkForApp(int circleId) {
    return String.format("%s/shareapp.do?factoryId=%d&parentId=%s&channel=%s",
        U.getConfig("api.host"), circleId, U.getConfig("app.parentId"), U.getConfig("app.channel"));
  }

  static String shareLinkForPost(String postId) {
    return String.format("%s/sharecontent.do?postVirtualId=%s&parentId=%s&channel=%s",
        U.getConfig("api.host"), postId, U.getConfig("app.parentId"), U.getConfig("app.channel"));
  }

  static String shareLinkForComment(String postId) {
    return shareLinkForPost(postId);
  }
  @Keep
  public class ShareCommentViewHolder {
    private BaseActivity mActivity;
    private ShareDialog mDialog;
    private Post mPost;
    private String mComment;

    @InjectView(R.id.aiv_qr_code)
    ImageView mIvQrCode;

    @InjectView(R.id.tv_qr_code)
    TextView mTvQrCode;

    @InjectView(R.id.rb_clipboard)
    RoundedButton mRbClipboard;

    ShareCommentViewHolder(BaseActivity activity, ShareDialog dialog, Post post, String comment) {
      mActivity = activity;
      mDialog = dialog;
      mPost = post;
      mComment = comment;
      ButterKnife.inject(this, dialog);

      mIvQrCode.setVisibility(View.GONE);
      mTvQrCode.setVisibility(View.GONE);
      mRbClipboard.setVisibility(View.VISIBLE);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_msg", "comment");
      mShortener.shorten(shareLinkForComment(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareViaSMS.shareComment(mActivity, mPost, mComment, shareLinkForComment(mPost.id));
          } else {
            mShareViaSMS.shareComment(mActivity, mPost, mComment, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qq", "comment");
      mShortener.shorten(shareLinkForComment(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareToQQ.shareComment(mActivity, mPost, mComment, shareLinkForComment(mPost.id));
          } else {
            mShareToQQ.shareComment(mActivity, mPost, mComment, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qzone)
    void onQzoneClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qzone", "comment");
      mShortener.shorten(shareLinkForComment(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareToQzone.shareComment(mActivity, mPost, mComment, shareLinkForComment(mPost.id));
          } else {
            mShareToQzone.shareComment(mActivity, mPost, mComment, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.rb_clipboard)
    void onRbClipboardClicked() {
      mShortener.shorten(shareLinkForComment(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          final ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
          if (shorten == null) {
            cm.setText(shareLinkForPost(mPost.id));
          } else {
            cm.setText(shorten);
          }
          U.showToast("已经复制至剪贴板");
        }
      });
      mDialog.dismiss();
    }
  }

  @Keep
  public class SharePostViewHolder {
    private BaseActivity mActivity;
    private ShareDialog mDialog;
    private Post mPost;

    @InjectView(R.id.aiv_qr_code)
    ImageView mIvQrCode;

    @InjectView(R.id.tv_qr_code)
    TextView mTvQrCode;

    @InjectView(R.id.rb_clipboard)
    RoundedButton mRbClipboard;

    SharePostViewHolder(BaseActivity activity, ShareDialog dialog, Post post) {
      mActivity = activity;
      mDialog = dialog;
      mPost = post;
      ButterKnife.inject(this, dialog);

      mIvQrCode.setVisibility(View.GONE);
      mTvQrCode.setVisibility(View.GONE);
      mRbClipboard.setVisibility(View.VISIBLE);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_msg", "post");
      mShortener.shorten(shareLinkForPost(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareViaSMS.sharePost(mActivity, mPost, shareLinkForPost(mPost.id));
          } else {
            mShareViaSMS.sharePost(mActivity, mPost, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qq", "post");
      mShortener.shorten(shareLinkForPost(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareToQQ.sharePost(mActivity, mPost, shareLinkForPost(mPost.id));
          } else {
            mShareToQQ.sharePost(mActivity, mPost, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qzone)
    void onQzoneClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qzone", "post");
      mShortener.shorten(shareLinkForPost(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareToQzone.sharePost(mActivity, mPost, shareLinkForPost(mPost.id));
          } else {
            mShareToQzone.sharePost(mActivity, mPost, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.rb_clipboard)
    void onRbClipboardClicked() {
      mShortener.shorten(shareLinkForPost(mPost.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          final ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
          if (shorten == null) {
            cm.setText(shareLinkForPost(mPost.id));
          } else {
            cm.setText(shorten);
          }
          U.showToast("已经复制至剪贴板");
        }
      });
      mDialog.dismiss();
    }
  }

  @Keep
  public class ShareAppViewHolder {
    private BaseActivity mActivity;
    private ShareDialog mDialog;
    private Circle mCircle;

    ShareAppViewHolder(BaseActivity activity, ShareDialog dialog, Circle circle) {
      mActivity = activity;
      mDialog = dialog;
      mCircle = circle;
      ButterKnife.inject(this, dialog);
    }

    @OnClick (R.id.tv_sms)
    void onTvSmsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_msg", "app");
      mShortener.shorten(shareLinkForApp(mCircle.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareViaSMS.shareApp(mActivity, mCircle, shareLinkForApp(mCircle.id));
          } else {
            mShareViaSMS.shareApp(mActivity, mCircle, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qq_friends)
    void onQQFriendsClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qq", "app");
      mShortener.shorten(shareLinkForApp(mCircle.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareToQQ.shareApp(mActivity, mCircle, shareLinkForApp(mCircle.id));
          } else {
            mShareToQQ.shareApp(mActivity, mCircle, shorten);
          }
        }
      });
      mDialog.dismiss();
    }

    @OnClick (R.id.tv_qzone)
    void onQzoneClicked() {
      U.getAnalyser().trackEvent(mActivity, "share_by_qzone", "app");
      mShortener.shorten(shareLinkForApp(mCircle.id), new Shortener.Callback() {
        @Override
        public void onShorten(String shorten) {
          if (shorten == null) {
            mShareToQzone.shareApp(mActivity, mCircle, shareLinkForApp(mCircle.id));
          } else {
            mShareToQzone.shareApp(mActivity, mCircle, shorten);
          }
        }
      });
      mDialog.dismiss();
    }
  }
}
