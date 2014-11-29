package com.utree.eightysix.app.post;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.BasePostView;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.feed.event.PostPostPraiseEvent;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.app.tag.TagTabActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.TagView;

import java.util.List;

/**
 * This is the post view in PostActivity
 *
 * @author simon
 * @see PostActivity
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

  @InjectView (R.id.tv_tag_1)
  public TagView mTvTag1;

  @InjectView (R.id.tv_tag_2)
  public TagView mTvTag2;

  @InjectView (R.id.tv_tag_3)
  public TagView mTvTag3;

  private Post mPost;

  public PostPostView(Context context) {
    this(context, null, 0);
  }

  public PostPostView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PostPostView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs);

    LayoutInflater.from(context).inflate(R.layout.item_post_post, this);
    ButterKnife.inject(this, this);

    M.getRegisterHelper().register(this);

    setPostTheme(Color.BLACK);
  }

  @OnClick (R.id.tv_tag_1)
  public void onTvTag1Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(0));
  }

  @OnClick (R.id.tv_tag_2)
  public void onTvTag2Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(1));
  }

  @OnClick (R.id.tv_tag_3)
  public void onTvTag3Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(2));
  }

  @OnClick(R.id.tv_source)
  public void onTvSourceClicked() {
    if (mPost.viewType == 8 || (mPost.sourceType == 0 && (mPost.viewType == 3 || mPost.viewType == 4))) {
      if (mPost.userCurrFactoryId == mPost.factoryId) {
        HomeActivity.start(getContext(), 0);
      } else {
        FeedActivity.start(getContext(), mPost.factoryId);
      }
    }
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

    mTvTag1.setText("");
    mTvTag2.setText("");
    mTvTag3.setText("");

    List<Tag> tags = mPost.tags;
    if (tags != null) {
      for (int i = 0; i < tags.size(); i++) {
        Tag g = tags.get(i);
        switch (i) {
          case 0:
            mTvTag1.setText("#" + g.content);
            break;
          case 1:
            mTvTag2.setText("#" + g.content);
            break;
          case 2:
            mTvTag3.setText("#" + g.content);
            break;
        }
      }
    }
  }

  @OnClick (R.id.tv_praise)
  public void onTvPraiseClicked() {
    if (mPost == null) return;
    if (mPost.praised != 1) {
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
