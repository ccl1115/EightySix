package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.PostPostPraiseEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.PostDeleteRequest;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;

/**
 * This is the post view in PostActivity
 *
 * @author simon
 * @see com.utree.eightysix.app.feed.PostActivity
 */
public class PostPostView extends BasePostView {

  private static int sPostLength = U.getConfigInt("post.length");

  @InjectView (R.id.aiv_bg)
  public AsyncImageView mAivBg;

  @InjectView (R.id.tv_content)
  public TextView mTvContent;

  @InjectView (R.id.tv_source)
  public TextView mTvSource;

  @InjectView (R.id.tv_comment)
  public TextView mTvComment;

  @InjectView (R.id.tv_praise)
  public TextView mTvPraise;

  @InjectView (R.id.iv_close)
  public ImageView mIvClose;

  @InjectView (R.id.iv_more)
  public ImageView mIvMore;

  private Post mPost;

  private int mCloseRes;
  private int mMoreRes;

  public PostPostView(Context context) {
    this(context, null, 0);
  }

  public PostPostView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PostPostView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    LayoutInflater.from(context).inflate(R.layout.item_post_post, this);
    ButterKnife.inject(this, this);

    M.getRegisterHelper().register(this);

    setPostTheme(Color.BLACK);
  }

  public void setData(Post post) {
    mPost = post;

    if (mPost == null) {
      return;
    }

    mTvContent.setText(mPost.content.length() > sPostLength ? post.content.substring(0, sPostLength) : post.content);
    if (mPost.comments > 0) {
      mTvComment.setText(String.valueOf(post.comments));
    } else {
      mTvComment.setText("");
    }
    mTvPraise.setText(String.valueOf(post.praise));
    
    if (mPost.isRepost == 1) {
      mTvSource.setText("转自" + mPost.source);
    } else {
      mTvSource.setText(mPost.source);
    }

    if (!TextUtils.isEmpty(mPost.bgUrl)) {
      mAivBg.setUrl(mPost.bgUrl);
      mAivBg.setBackgroundColor(Color.TRANSPARENT);
    } else {
      mAivBg.setUrl(null);
      mAivBg.setBackgroundColor(ColorUtil.strToColor(mPost.bgColor));
    }

    mTvComment.setCompoundDrawablesWithIntrinsicBounds(mCommentRes, 0, 0, 0);

    if (mPost.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_pressed, 0, 0, 0);
    } else if (mPost.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartRes, 0, 0, 0);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartOutlineRes, 0, 0, 0);
    }
  }

  @OnClick (R.id.iv_close)
  public void onIvCloseClicked() {
    U.getAnalyser().trackEvent(U.getContext(), "post_close", "post_close");
    ((PostActivity) getContext()).finishOrShowQuitConfirmDialog();
  }

  @OnClick (R.id.iv_more)
  public void onIvMoreClicked() {
    if (mPost == null) return;

    U.getAnalyser().trackEvent(U.getContext(), "post_more", "post_more");
    new AlertDialog.Builder(getContext()).setTitle(U.gs(R.string.post_action))
        .setItems(new String[]{U.gs(R.string.share), U.gs(R.string.report),
                mPost.praised == 1 ? U.gs(R.string.unlike) : U.gs(R.string.like),
                U.gs(R.string.delete)},
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_share", "post_more_share");
                    U.getShareManager().sharePostDialog(((Activity) getContext()), mPost).show();
                    break;
                  case 1:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_report", "post_more_report");
                    new ReportDialog(getContext(), mPost.id).show();
                    break;
                  case 2:
                    if (mPost == null) return;
                    if (mPost.praised == 1) {
                      U.getAnalyser().trackEvent(U.getContext(), "post_more_praise", "cancel");
                      doCancelPraise();
                    } else {
                      U.getAnalyser().trackEvent(U.getContext(), "post_more_praise", "praise");
                      doPraise();
                    }
                    ((BaseAdapter) ((AdapterView) getParent()).getAdapter()).notifyDataSetChanged();
                    break;
                  case 3:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_delete", "post_more_delete");
                    U.getBus().post(new PostDeleteRequest(mPost.id));
                    break;
                }
              }
            }).create().show();
  }

  @OnClick (R.id.tv_praise)
  public void onTvPraiseClicked() {
    if (mPost == null) return;
    if (mPost.praised == 1) {
      U.getAnalyser().trackEvent(U.getContext(), "post_praise", "cancel");
      doCancelPraise();
    } else {
      U.getAnalyser().trackEvent(U.getContext(), "post_praise", "praise");
      doPraise();
    }
    ((BaseAdapter) ((AdapterView) getParent()).getAdapter()).notifyDataSetChanged();
  }

  protected void doPraise() {
    AnimatorSet praiseAnimator = new AnimatorSet();
    praiseAnimator.setDuration(800);
    praiseAnimator.playTogether(
        ObjectAnimator.ofFloat(mTvPraise, "scaleX", 1, 1.2f, 0.8f, 1),
        ObjectAnimator.ofFloat(mTvPraise, "scaleY", 1, 1.2f, 0.8f, 1)
    );
    praiseAnimator.start();
    mPost.praised = 1;
    mPost.praise++;
    U.getBus().post(new PostPostPraiseEvent(mPost, false));
  }

  private void doCancelPraise() {
    AnimatorSet unlikeAnimator = new AnimatorSet();
    unlikeAnimator.setDuration(500);
    unlikeAnimator.playTogether(
        ObjectAnimator.ofFloat(mTvPraise, "scaleX", 1, 0.8f, 1),
        ObjectAnimator.ofFloat(mTvPraise, "scaleY", 1, 0.8f, 1)
    );
    unlikeAnimator.start();
    mPost.praised = 0;
    mPost.praise--;
    U.getBus().post(new PostPostPraiseEvent(mPost, true));
  }

  @Override
  protected void setPostTheme(int color) {
    super.setPostTheme(color);

    if (mMonoColor == Color.WHITE) {
      mCloseRes = R.drawable.ic_action_post_close;
      mMoreRes = R.drawable.ic_action_post_more;
    } else {
      mCloseRes = R.drawable.ic_black_action_post_close;
      mMoreRes = R.drawable.ic_black_action_post_more;
    }

    mTvComment.setTextColor(mMonoColor);
    mTvContent.setTextColor(mMonoColor);
    mTvPraise.setTextColor(mMonoColor);
    mTvSource.setTextColor(mMonoColor);

    if (mPost == null) return;

    if (mPost.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_pressed, 0, 0, 0);
    } else if (mPost.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartRes, 0, 0, 0);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartOutlineRes, 0, 0, 0);
    }

    mTvComment.setCompoundDrawablesWithIntrinsicBounds(mCommentRes, 0, 0, 0);

    mIvClose.setImageResource(mCloseRes);
    mIvMore.setImageResource(mMoreRes);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    super.onMeasure(widthMeasureSpec, widthSize + MeasureSpec.EXACTLY);
  }

  @Override
  protected void onDetachedFromWindow() {
    M.getRegisterHelper().unregister(this);
    super.onDetachedFromWindow();
  }
}
