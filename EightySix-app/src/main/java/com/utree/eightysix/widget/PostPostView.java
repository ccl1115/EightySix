package com.utree.eightysix.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ShareUtils;
import com.utree.eightysix.utils.Utils;

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

  private AlertDialog mPostContextDialog;

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

    if (post == null) {
      clear();
      return;
    }

    mTvContent.setText(mPost.content.length() > sPostLength ? post.content.substring(0, sPostLength) : post.content);
    mTvComment.setText(String.valueOf(post.comments));
    mTvPraise.setText(String.valueOf(post.praise));
    mTvSource.setText(mPost.source);

    if (!TextUtils.isEmpty(mPost.bgUrl)) {
      mAivBg.setUrl(mPost.bgUrl);
      mTvContent.setBackgroundColor(Color.TRANSPARENT);
    } else {
      mAivBg.setUrl(null);
      mTvContent.setBackgroundColor(Utils.strToColor(mPost.bgColor));
    }

    if (mPost.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(U.gd(R.drawable.ic_heart_red_pressed), null, null, null);
    } else if (mPost.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(U.gd(R.drawable.ic_heart_white_normal), null, null, null);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(U.gd(R.drawable.ic_heart_outline_normal), null, null, null);
    }
  }

  private void clear() {
    mTvContent.setText("");
    mTvComment.setBackgroundColor(Color.WHITE);
    mTvComment.setText("");
    mTvPraise.setText("");
    mTvSource.setText("");
  }

  @OnClick (R.id.iv_close)
  public void onIvCloseClicked() {
    ((Activity) getContext()).finish();
  }

  @OnClick (R.id.iv_more)
  public void onIvMoreClicked() {
    if (mPost == null) return;
    new AlertDialog.Builder(getContext()).setTitle(U.gs(R.string.post_action))
        .setItems(new String[]{U.gs(R.string.share), U.gs(R.string.report),
                mPost.praised == 1 ? U.gs(R.string.unlike) : U.gs(R.string.like),
                U.gs(R.string.delete)},
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    ShareUtils.sharePostToQQ(((Activity) getContext()), mPost);
                    break;
                  case 1:
                    U.showToast("TODO report");
                    break;
                  case 2:
                    onTvPraiseClicked();
                    break;
                  case 3:
                    U.showToast("TODO delete");
                    break;
                }
              }
            }).create().show();

  }

  @OnClick (R.id.tv_praise)
  public void onTvPraiseClicked() {
    if (mPost == null) return;
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

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    super.onMeasure(widthMeasureSpec, (int) (widthSize * 1.1f) + MeasureSpec.EXACTLY);
  }


}
