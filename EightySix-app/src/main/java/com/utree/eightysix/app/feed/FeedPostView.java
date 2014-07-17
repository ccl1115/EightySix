package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.aliyun.android.util.MD5Util;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.drawable.SmallGearsDrawable;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.ShareUtils;
import com.utree.eightysix.widget.AsyncImageView;
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

  @InjectView (R.id.ll_panel)
  public LinearLayout mLlPanel;

  @InjectView (R.id.ll_item)
  public LinearLayout mLlItem;

  @InjectView (R.id.fl_content)
  public FrameLayout mFlContent;

  private int mFactoryId;

  private SmallGearsDrawable mGearsDrawable;

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

    U.getBus().register(this);

    mGearsDrawable = new SmallGearsDrawable();
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (!TextUtils.isEmpty(mPost.bgUrl)) {
      if (ImageUtils.getUrlHash(mPost.bgUrl).equals(event.getHash())) {
        mFlContent.setVisibility(VISIBLE);

        if (TextUtils.isEmpty(mPost.comment)) {
          mLlComment.setVisibility(GONE);
        } else {
          mLlComment.setVisibility(VISIBLE);
        }

        mLlItem.setBackgroundDrawable(null);

        ColorUtil.asyncThemedColor(event.getBitmap());
      }
    }
  }

  @Subscribe
  public void onThemedColorEvent(ColorUtil.ThemedColorEvent event) {
    if (!TextUtils.isEmpty(mPost.bgUrl)) {
      if (event.getBitmap().equals(ImageUtils.getFromMemByUrl(mPost.bgUrl))) {
        setPostTheme(event.getColor());
        ListView parent = (ListView) getParent();
        if (parent != null) {
          ((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
        }
      }
    }
  }

  @Override
  protected void setPostTheme(int color) {
    super.setPostTheme(color);

    mTvComment.setTextColor(mMonoColor);
    mTvContent.setTextColor(mMonoColor);
    mTvPraise.setTextColor(mMonoColor);
    mTvSource.setTextColor(mMonoColor);
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

  public void setData(int factoryId, Post post) {
    mFactoryId = factoryId;

    if (mPost != null && mPost.equals(post)) {
      return;
    }

    mPost = post;

    if (mPost == null) {
      return;
    }

    if (TextUtils.isEmpty(mPost.bgUrl)) {
      setPostTheme(ColorUtil.strToColor(mPost.bgColor));
    } else {
      Bitmap fromMemByUrl = ImageUtils.getFromMemByUrl(mPost.bgUrl);
      if (fromMemByUrl != null) {
        ColorUtil.asyncThemedColor(fromMemByUrl);
      } else {
        mTvComment.setTextColor(Color.TRANSPARENT);
        mTvPraise.setTextColor(Color.TRANSPARENT);
        mTvContent.setTextColor(Color.TRANSPARENT);
        mTvSource.setTextColor(Color.TRANSPARENT);
      }
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
      if (ImageUtils.getFromMem(MD5Util.getMD5String(post.bgUrl.getBytes()).toLowerCase()) == null) {
        mFlContent.setVisibility(INVISIBLE);
        mLlComment.setVisibility(INVISIBLE);
        mLlItem.setBackgroundDrawable(mGearsDrawable);
      }
      mTvContent.setBackgroundColor(Color.TRANSPARENT);
      mAivBg.setUrl(post.bgUrl);
    } else {
      mFlContent.setVisibility(VISIBLE);
      mLlComment.setVisibility(VISIBLE);
      mTvContent.setBackgroundColor(ColorUtil.strToColor(post.bgColor));
      mLlItem.setBackgroundDrawable(null);
      mAivBg.setUrl(null);
    }

    if (post.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_pressed, 0, 0, 0);
    } else if (post.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartRes, 0, 0, 0);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(mHeartOutlineRes, 0, 0, 0);
    }

    mTvComment.setCompoundDrawablesWithIntrinsicBounds(mCommentRes, 0, 0, 0);

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
    ((BaseAdapter) ((AdapterView) getParent()).getAdapter()).notifyDataSetChanged();
  }

  @Subscribe
  public void onListViewScrollStateIdled(ListViewScrollStateIdledEvent event) {
    if (mIvShare.getVisibility() == INVISIBLE && getTop() >= 0) {
      showShareButton();
    }
  }

  protected void showShareButton() {
    mIvShare.setVisibility(VISIBLE);
    ObjectAnimator animator = ObjectAnimator.ofFloat(mIvShare, "alpha", 0f, 1f);
    animator.setDuration(500);
    animator.start();
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    U.getBus().unregister(this);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    if (mLlComment.getVisibility() == VISIBLE) {
      widthSize += mLlComment.getMeasuredHeight();
    }
    super.onMeasure(widthMeasureSpec, widthSize - U.dp2px(16) + MeasureSpec.EXACTLY);
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
