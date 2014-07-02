package com.utree.eightysix.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ShareUtils;
import com.utree.eightysix.utils.Utils;

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

  @InjectView (R.id.iv_share)
  public ImageView mIvShare;

  @InjectView (R.id.aiv_bg)
  public AsyncImageView mAivBg;

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

  public void setContent(String content) {
    mTvContent.setText(content);
  }

  public CharSequence getSource() {
    return mTvSource.getText();
  }

  public void setSource(String source) {
    mTvSource.setText(source);
  }

  public CharSequence getPraise() {
    return mTvPraise.getText();
  }

  public void setPraise(String praise) {
    mTvPraise.setText(praise);
  }

  public CharSequence getComment() {
    return mTvComment.getText();
  }

  public void setComment(String comment) {
    mTvComment.setText(comment);
  }

  public TextView getLastComment() {
    return mTvLastComment;
  }

  public void setLastComment(String lastComment) {
    mTvLastComment.setText(lastComment);
  }

  public void setData(Post post) {
    mPost = post;

    setContent(post.content.length() > sPostLength ? post.content.substring(0, sPostLength) : post.content);
    setComment(String.valueOf(post.comments));
    setPraise(String.valueOf(post.praise));
    setSource(post.source);
    //setLastComment(post.comment.toString());

    if (!TextUtils.isEmpty(post.bgUrl)) {
      mAivBg.setUrl(post.bgUrl);
      mTvContent.setBackgroundColor(Color.TRANSPARENT);
    } else {
      mAivBg.setUrl(null);
      mTvContent.setBackgroundColor(Utils.strToColor(post.bgColor));
    }

    if (post.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_red_pressed), null, null, null);
    } else if (post.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_white_normal), null, null, null);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_outline_normal), null, null, null);
    }

    if (getTop() <= 0) {
      mIvShare.setVisibility(INVISIBLE);
    }

  }

  @OnClick (R.id.iv_share)
  public void onIvShareClicked() {
    ShareUtils.sharePostToQQ((Activity) getContext(), mPost);
  }

  @OnClick (R.id.tv_praise)
  public void onTvPraiseClicked() {
    Toast.makeText(getContext(), "TODO praise request", Toast.LENGTH_SHORT).show();
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
