package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.PostPostPraiseEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.PostDeleteRequest;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.ShareUtils;
import com.utree.eightysix.widget.AsyncImageView;
import de.akquinet.android.androlog.Log;

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
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (mPost.bgUrl != null) {
      if (ImageUtils.getUrlHash(mPost.bgUrl).equals(event.getHash())) {
        mAivBg.setVisibility(VISIBLE);

        ColorUtil.asyncThemedColor(event.getBitmap());
      }
    }
  }

  @Subscribe
  public void onThemedColorEvent(ColorUtil.ThemedColorEvent event) {
    Bitmap fromMemByUrl = ImageUtils.getFromMemByUrl(mPost.bgUrl);
    Log.d("PostPostView", "bitmap == null is" + (fromMemByUrl == null));
    if (event.getBitmap().equals(fromMemByUrl)) {
      setPostTheme(event.getColor());
    }
  }

  public void setData(Post post) {
    mPost = post;

    if (mPost == null) {
      clear();
      return;
    }

    if (mPost.bgUrl == null) {
      setPostTheme(ColorUtil.strToColor(mPost.bgColor));
    }

    mIvClose.setImageResource(mCloseRes);
    mIvMore.setImageResource(mMoreRes);

    mTvContent.setText(mPost.content.length() > sPostLength ? post.content.substring(0, sPostLength) : post.content);
    if (post.comments > 0) {
      mTvComment.setText(String.valueOf(post.comments));
    } else {
      mTvComment.setText("");
    }
    mTvPraise.setText(String.valueOf(post.praise));
    mTvSource.setText(mPost.source);

    if (!TextUtils.isEmpty(mPost.bgUrl)) {
      mAivBg.setUrl(mPost.bgUrl);
      mTvContent.setBackgroundColor(Color.TRANSPARENT);
    } else {
      mAivBg.setUrl(null);
      mTvContent.setBackgroundColor(ColorUtil.strToColor(mPost.bgColor));
    }


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
      U.getBus().post(new PostPostPraiseEvent(mPost, false));
    }
    ((BaseAdapter) ((AdapterView) getParent()).getAdapter()).notifyDataSetChanged();
  }

  @Override
  protected void setPostTheme(int color) {
    super.setPostTheme(color);

    if (mMonoColor == Color.WHITE) {
      mCloseRes = R.drawable.ic_black_action_post_close;
      mMoreRes = R.drawable.ic_black_action_post_more;
    } else {
      mCloseRes = R.drawable.ic_action_post_close;
      mMoreRes = R.drawable.ic_action_post_more;
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    super.onMeasure(widthMeasureSpec, widthSize + MeasureSpec.EXACTLY);
  }

  private void clear() {
    mTvContent.setText("");
    mTvComment.setBackgroundColor(Color.WHITE);
    mTvComment.setText("");
    mTvPraise.setText("");
    mTvSource.setText("");
  }


}
