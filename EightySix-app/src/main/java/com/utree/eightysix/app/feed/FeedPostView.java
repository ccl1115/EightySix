package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.GearsView;
import de.akquinet.android.androlog.Log;

/**
 */
public class FeedPostView extends BasePostView {

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

  @InjectView (R.id.ll_comment)
  public LinearLayout mLlComment;

  @InjectView (R.id.fl_grid_panel)
  public LinearLayout mLlPanel;

  @InjectView (R.id.rl_item)
  public LinearLayout mLlItem;

  @InjectView (R.id.fl_content)
  public FrameLayout mFlContent;

  @InjectView (R.id.gv_loading)
  public GearsView mGvLoading;

  private Runnable mShareAnimation;

  private boolean mHasTipShown;

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

    M.getRegisterHelper().register(this);

    mShareAnimation = new Runnable() {
      @Override
      public void run() {
        mIvShare.setVisibility(VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mIvShare, "alpha", 0, 1f);
        alpha.setDuration(500);
        alpha.start();
      }
    };

    setPostTheme(Color.BLACK);
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (!TextUtils.isEmpty(mPost.bgUrl)) {
      mGvLoading.setVisibility(INVISIBLE);
    }
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

    String content = post.content.length() > sPostLength ? post.content.substring(0, sPostLength) : post.content;

    mTvContent.setText(content);
    if (post.comments > 0) {
      mTvComment.setText(String.valueOf(post.comments));
    } else {
      mTvComment.setText("");
    }
    mTvPraise.setText(String.valueOf(post.praise));
    mTvSource.setText(post.source);
    mTvLastComment.setText(post.comment);
    mTvLastCommentHead.setText(post.commentHead);
    mTvLastCommentTail.setText(post.commentTail);


    if (!TextUtils.isEmpty(post.bgUrl)) {
      if (ImageUtils.getFromMemByUrl(post.bgUrl) == null) {
        mGvLoading.setVisibility(VISIBLE);
        mFlContent.setBackgroundColor(Color.WHITE);
      } else {
        mGvLoading.setVisibility(INVISIBLE);
      }
      mAivBg.setUrl(post.bgUrl);
    } else {
      mGvLoading.setVisibility(INVISIBLE);
      mFlContent.setBackgroundColor(ColorUtil.strToColor(post.bgColor));
      mAivBg.setUrl(null);
    }

    if (mPost.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_pressed, 0, 0, 0);
    } else if (mPost.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartRes, 0, 0, 0);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartOutlineRes, 0, 0, 0);
    }

    mTvComment.setCompoundDrawablesWithIntrinsicBounds(mCommentRes, 0, 0, 0);

    if (TextUtils.isEmpty(post.comment)) {
      mLlComment.setVisibility(GONE);
    } else {
      mLlComment.setVisibility(VISIBLE);
    }

    mIvShare.setVisibility(INVISIBLE);
    mIvShare.removeCallbacks(mShareAnimation);
    mIvShare.postDelayed(mShareAnimation, 500);
  }

  @OnClick (R.id.iv_share)
  public void onIvShareClicked() {
    U.getShareManager().sharePostDialog((Activity) getContext(), mPost.circle, mPost).show();
  }

  @OnClick (R.id.tv_praise)
  public void onTvPraiseClicked() {
    if (mPost.praised == 1) {
      U.getAnalyser().trackEvent(U.getContext(), "feed_post_praise", "cancel");
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
      U.getAnalyser().trackEvent(U.getContext(), "feed_post_praise", "praise");
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
    ((BaseAdapter) ((AdapterView) getParent()).getAdapter()).notifyDataSetChanged();
  }

  @Override
  protected void setPostTheme(int color) {
    super.setPostTheme(color);

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

    invalidate();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    if (mLlComment.getVisibility() == VISIBLE) {
      widthSize += U.dp2px(44);
    }
    super.onMeasure(widthMeasureSpec, widthSize - U.dp2px(16) + MeasureSpec.EXACTLY);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    M.getRegisterHelper().register(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    M.getRegisterHelper().unregister(this);
    super.onDetachedFromWindow();
  }

  public void showShareTipOverlay() {
    if (mHasTipShown) return;

    View view = LayoutInflater.from(getContext())
        .inflate(R.layout.overlay_tip_share, this, false);

    view.findViewById(R.id.ll_tip).setBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(8), Color.WHITE));
    mFlContent.addView(view);

    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mFlContent.removeView(v);
        mHasTipShown = false;
      }
    });

    mHasTipShown = true;
  }

  public void showSourceTipOverlay() {
    if (mHasTipShown) return;

    View view = LayoutInflater.from(getContext())
        .inflate(R.layout.overlay_tip_source, this, false);

    view.findViewById(R.id.ll_tip).setBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(8), Color.WHITE));
    mFlContent.addView(view);

    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mFlContent.removeView(v);
        mHasTipShown = false;
      }
    });
    mHasTipShown = true;
  }

  public void showPraiseTipOverlay() {
    if (mHasTipShown) return;

    View view = LayoutInflater.from(getContext())
        .inflate(R.layout.overlay_tip_praise, this, false);

    view.findViewById(R.id.ll_tip).setBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(8), Color.WHITE));
    mFlContent.addView(view);

    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mFlContent.removeView(v);
        mHasTipShown = false;
      }
    });

    mHasTipShown = true;
  }
}
