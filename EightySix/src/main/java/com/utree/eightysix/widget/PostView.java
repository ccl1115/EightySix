package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.response.data.Post;
import java.util.Random;

/**
 */
public class PostView extends RelativeLayout {

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

  public PostView(Context context) {
    this(context, null, 0);
  }

  public PostView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PostView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    inflate(context, R.layout.item_post, this);
    U.viewBinding(this, this);

    mIvShare.setBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(2), getResources().getColorStateList(R.color.apptheme_transparent_bg)));

    mTvContent.setBackgroundColor(new Random().nextInt());
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
    setContent(post.content.length() > 140 ? post.content.substring(0, sPostLength) : post.content);
    setComment(String.valueOf(post.comments));
    setPraise(String.valueOf(post.praise));
    setSource(post.source);
    //setLastComment(post.comment.toString());

    if (post.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_red_pressed), null, null, null);
    } else if (post.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_white_normal), null, null, null);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_outline_normal), null, null, null);
    }

    mIvShare.setVisibility(INVISIBLE);

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
    super.onMeasure(widthMeasureSpec, (int) (widthSize * 1.1f + MeasureSpec.EXACTLY));
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
