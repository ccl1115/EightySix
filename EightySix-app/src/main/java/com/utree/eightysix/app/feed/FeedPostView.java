package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.FeedPostCancelPraiseEvent;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.utils.ShareUtils;
import com.utree.eightysix.utils.Utils;
import com.utree.eightysix.widget.AsyncImageView;

/**
 */
public class FeedPostView extends RelativeLayout {

  private static int sPostLength = U.getConfigInt("post.length");

  @InjectView (R.id.tv_content)
  public TextView mTvContent;

  @InjectView (R.id.tv_source)
  public TextView mTvSource;

  @InjectView (R.id.tv_praise)
  public TextView mTvPraise;

  @InjectView (R.id.tv_comment)
  public TextView mTvComment;

  @InjectView (R.id.tv_last_comment)
  public TextView mTvLastComment;

  @InjectView (R.id.tv_last_comment_head)
  public TextView mTvLastCommentHead;

  @InjectView (R.id.tv_last_comment_tail)
  public TextView mTvLastCommentTail;

  @InjectView (R.id.iv_share)
  public ImageView mIvShare;

  @InjectView (R.id.aiv_bg)
  public AsyncImageView mAivBg;

  @InjectView(R.id.ll_comment)
  public LinearLayout mLlComment;

  private Post mPost;

  public FeedPostView(Context context) {
    this(context, null, 0);
  }

  public FeedPostView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FeedPostView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    inflate(context, R.layout.item_feed_post, this);
    U.viewBinding(this, this);

    mIvShare.setBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(2), getResources().getColorStateList(R.color.apptheme_transparent_bg)));
  }

  public ImageView getIvShare() {
    return mIvShare;
  }

  public CharSequence getContent() {
    return mTvContent.getText();
  }

  public CharSequence getSource() {
    return mTvSource.getText();
  }

  public CharSequence getPraise() {
    return mTvPraise.getText();
  }

  public CharSequence getComment() {
    return mTvComment.getText();
  }

  public TextView getLastComment() {
    return mTvLastComment;
  }

  public void setData(Post post) {
    mPost = post;

    if (mPost == null) {
      return;
    }

    int color = Utils.monochromizing(Utils.strToColor(mPost.bgColor));

    mTvComment.setTextColor(color);
    mTvContent.setTextColor(color);
    mTvPraise.setTextColor(color);
    mTvSource.setTextColor(color);

    String content = post.content.length() > sPostLength ? post.content.substring(0, sPostLength) : post.content;

    mTvContent.setText(content);
    mTvComment.setText(String.valueOf(post.comments));
    mTvPraise.setText(String.valueOf(post.praise));
    mTvSource.setText(post.source);
    mTvLastComment.setText(post.comment);
    mTvLastCommentHead.setText(post.commentHead);
    mTvLastCommentTail.setText(post.commentTail);


    if (!TextUtils.isEmpty(post.bgUrl)) {
      mAivBg.setUrl(post.bgUrl);
      mTvContent.setBackgroundColor(Color.TRANSPARENT);
    } else {
      mAivBg.setUrl(null);
      mTvContent.setBackgroundColor(Utils.strToColor(post.bgColor));
    }

    if (post.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_heart_red_pressed), null, null, null);
    } else if (post.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_heart_white_normal), null, null, null);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_heart_outline_normal), null, null, null);
    }

    if (getTop() <= 0) {
      mIvShare.setVisibility(INVISIBLE);
    }

    if (TextUtils.isEmpty(post.comment)) {
      mLlComment.setVisibility(GONE);
    } else {
      mLlComment.setVisibility(VISIBLE);
    }
  }

  @OnClick (R.id.iv_share)
  public void onIvShareClicked() {
    ShareUtils.sharePostToQQ((Activity) getContext(), mPost);
  }

  @OnClick (R.id.tv_praise)
  public void onTvPraiseClicked() {
    if (mPost.praised == 1) {
      AnimatorSet unlikeAnimator = new AnimatorSet();
      unlikeAnimator.setDuration(500);
      unlikeAnimator.playTogether(
          ObjectAnimator.ofFloat(mTvPraise, "scaleX", 1, 0.8f, 1),
          ObjectAnimator.ofFloat(mTvPraise, "scaleY", 1, 0.8f, 1)
      );
      unlikeAnimator.start();
      mPost.praised = 0;
      mPost.praise--;
      U.getBus().post(new FeedPostPraiseEvent(mPost, true));
    } else {
      AnimatorSet praiseAnimator = new AnimatorSet();
      praiseAnimator.setDuration(800);
      praiseAnimator.playTogether(
          ObjectAnimator.ofFloat(mTvPraise, "scaleX", 1, 1.2f, 0.8f, 1),
          ObjectAnimator.ofFloat(mTvPraise, "scaleY", 1, 1.2f, 0.8f, 1)
      );
      praiseAnimator.start();
      mPost.praised = 1;
      mPost.praise++;
      U.getBus().post(new FeedPostPraiseEvent(mPost, false));
    }
    U.getBus().post(new AdapterDataSetChangedEvent());
  }

  @Subscribe
  public void onListViewScrollStateIdled(ListViewScrollStateIdledEvent event) {
    if (mIvShare.getVisibility() == INVISIBLE && getTop() >= 0) {
      mIvShare.setVisibility(VISIBLE);
      ObjectAnimator animator = ObjectAnimator.ofFloat(mIvShare, "alpha", 0f, 1f);
      animator.setDuration(500);
      animator.start();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    super.onMeasure(widthMeasureSpec, widthSize + MeasureSpec.EXACTLY);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    U.getBus().register(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    U.getBus().unregister(this);
  }

}
