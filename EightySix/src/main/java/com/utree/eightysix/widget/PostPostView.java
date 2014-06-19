package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.response.data.Post;

/**
 * This is the post view in PostActivity
 *
 * @author simon
 * @see com.utree.eightysix.app.feed.PostActivity
 */
public class PostPostView extends FrameLayout {

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

  private Post mPost;

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
  }

  public void setData(Post post) {
    mPost = post;

    mTvContent.setText(post.content.length() > sPostLength ? post.content.substring(0, sPostLength) : post.content);
    mTvComment.setText(String.valueOf(post.comments));
    mTvPraise.setText(String.valueOf(post.praise));
    mTvSource.setText(post.source);

    if (!TextUtils.isEmpty(post.bgUrl)) {
      mAivBg.setUrl(post.bgUrl);
      mTvContent.setBackgroundColor(Color.TRANSPARENT);
    } else {
      mAivBg.setUrl(null);
      mTvContent.setBackgroundColor(post.bgColor);
    }

    if (post.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_red_pressed), null, null, null);
    } else if (post.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_white_normal), null, null, null);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_outline_normal), null, null, null);
    }
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
    ((BaseAdapter) ((ListView) getParent()).getAdapter()).notifyDataSetChanged();
  }

  @SuppressWarnings ("SuspiciousNameCombination")
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
  }
}
