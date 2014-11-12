package com.utree.eightysix.app.feed;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.app.region.FeedRegionAdapter;
import com.utree.eightysix.app.tag.TagTabActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.ViewHighlighter;
import java.util.List;

import static com.utree.eightysix.app.region.FeedRegionAdapter.DismissTipOverlayEvent.*;

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

  @InjectView (R.id.tv_hot)
  public TextView mTvHot;

  @InjectView (R.id.tv_tag_1)
  public TextView mTvTag1;

  @InjectView (R.id.tv_tag_2)
  public TextView mTvTag2;

  @InjectView (R.id.tv_tag_3)
  public TextView mTvTag3;

  @InjectView(R.id.ll_tags)
  public LinearLayout mLlTags;

  @OnClick(R.id.tv_tag_1)
  public void onTvTag1Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(0));
  }

  @OnClick(R.id.tv_tag_2)
  public void onTvTag2Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(1));
  }

  @OnClick(R.id.tv_tag_3)
  public void onTvTag3Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(2));
  }

  @OnClick(R.id.tv_source)
  public void onTvSourceClicked() {
    if (mPost.sourceType == 0 && (mPost.viewType == 3 || mPost.viewType == 4 || mPost.viewType == 8)) {
      if (mPost.userCurrFactoryId == mPost.factoryId) {
        HomeActivity.start(getContext(), 0);
      } else {
        FeedActivity.start(getContext(), mPost.factoryId);
      }
    }
  }

  private Runnable mShareAnimation;

  private int mCircleId;

  private View mTipShare;
  private View mTipSource;
  private View mTipPraise;
  private View mTipRepost;
  private View mTipTempName;
  private View mTipTags;

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

  public void setData(Post post, int circleId) {
    mPost = post;

    mCircleId = circleId;

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
    mTvLastComment.setText(post.comment);
    mTvLastCommentHead.setText(post.commentHead);
    mTvLastCommentTail.setText(post.commentTail);

    if (mPost.isRepost == 1) {
      mTvSource.setText("转自" + mPost.source);
    } else {
      mTvSource.setText(mPost.source);
    }

    if (mPost.isHot == 1) {
      mTvHot.setVisibility(VISIBLE);
    } else {
      mTvHot.setVisibility(GONE);
    }

    if (!TextUtils.isEmpty(post.bgUrl)) {
      mFlContent.setBackgroundColor(Color.WHITE);
      mAivBg.setVisibility(VISIBLE);
      mAivBg.setUrl(post.bgUrl);
    } else {
      mAivBg.setVisibility(INVISIBLE);
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

    mIvShare.setVisibility(INVISIBLE);
    mIvShare.removeCallbacks(mShareAnimation);
    mIvShare.postDelayed(mShareAnimation, 500);
  }

  @OnClick (R.id.iv_share)
  public void onIvShareClicked() {
    U.getShareManager().sharePostDialog((BaseActivity) getContext(), mPost).show();
  }

  @OnClick (R.id.tv_praise)
  public void onTvPraiseClicked() {
    if (mPost.praised != 1) {
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
    super.onMeasure(widthMeasureSpec, widthSize + MeasureSpec.EXACTLY);
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

  public void showShareTip() {
    if (mTipShare == null) {
      mTipShare = LayoutInflater.from(getContext())
          .inflate(R.layout.overlay_tip_share, this, false);

      mTipShare.setBackgroundDrawable(new BitmapDrawable(getResources(),
          new ViewHighlighter(mIvShare, mFlContent).genMask()));

      mTipShare.findViewById(R.id.ll_tip).setBackgroundDrawable(
          new RoundRectDrawable(U.dp2px(8), Color.WHITE));

      mFlContent.addView(mTipShare);

      mTipShare.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          view.setVisibility(GONE);
          U.getBus().post(new FeedRegionAdapter.DismissTipOverlayEvent(TYPE_SHARE));
          Env.setFirstRun("overlay_tip_share", false);
        }
      });
    } else {
      mTipShare.setVisibility(VISIBLE);
    }
  }

  public void hideShareTip() {
    hideTip(mTipShare);
  }

  public void showSourceTip() {
    if (mTipSource == null) {
      mTipSource = LayoutInflater.from(getContext())
          .inflate(R.layout.overlay_tip_source, this, false);

      mTipSource.setBackgroundDrawable(new BitmapDrawable(getResources(),
          new ViewHighlighter(mTvSource, mFlContent).genMask()));

      mTipSource.findViewById(R.id.ll_tip).setBackgroundDrawable(
          new RoundRectDrawable(U.dp2px(8), Color.WHITE));

      mFlContent.addView(mTipSource);

      mTipSource.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          view.setVisibility(GONE);
          U.getBus().post(new FeedRegionAdapter.DismissTipOverlayEvent(TYPE_SOURCE));
          Env.setFirstRun("overlay_tip_source", false);
        }
      });
    } else {
      mTipSource.setVisibility(VISIBLE);
    }
  }

  public void hideSourceTip() {
    hideTip(mTipSource);
  }

  public void showPraiseTip() {
    if (mTipPraise == null) {
      mTipPraise = LayoutInflater.from(getContext())
          .inflate(R.layout.overlay_tip_praise, this, false);

      mTipPraise.setBackgroundDrawable(new BitmapDrawable(getResources(),
          new ViewHighlighter(mTvPraise, mFlContent).genMask()));

      mTipPraise.findViewById(R.id.ll_tip).setBackgroundDrawable(
          new RoundRectDrawable(U.dp2px(8), Color.WHITE));

      mFlContent.addView(mTipPraise);

      mTipPraise.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          view.setVisibility(GONE);
          U.getBus().post(new FeedRegionAdapter.DismissTipOverlayEvent(TYPE_PRAISE));
          Env.setFirstRun("overlay_tip_praise", false);
        }
      });
    } else {
      mTipPraise.setVisibility(VISIBLE);
    }
  }

  public void hidePraiseTip() {
    hideTip(mTipPraise);
  }

  public void showRepostTip() {
    if (mTipRepost == null) {
      mTipRepost = LayoutInflater.from(getContext())
          .inflate(R.layout.overlay_tip_repost, this, false);

      mTipRepost.setBackgroundDrawable(new BitmapDrawable(getResources(),
          new ViewHighlighter(mTvSource, mFlContent).genMask()));

      mTipRepost.findViewById(R.id.ll_tip).setBackgroundDrawable(
          new RoundRectDrawable(U.dp2px(8), Color.WHITE));

      mFlContent.addView(mTipRepost);

      mTipRepost.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          view.setVisibility(GONE);
          U.getBus().post(new FeedRegionAdapter.DismissTipOverlayEvent(TYPE_REPOST));
          Env.setFirstRun("overlay_tip_repost", false);
        }
      });
    } else {
      mTipRepost.setVisibility(VISIBLE);
    }
  }

  public void hideRepostTip() {
    hideTip(mTipRepost);
  }

  public void showTempNameTip() {
    if (mTipTempName == null) {
      mTipTempName = LayoutInflater.from(getContext())
          .inflate(R.layout.overlay_tip_temp_name, this, false);

      mTipTempName.setBackgroundDrawable(new BitmapDrawable(getResources(),
          new ViewHighlighter(mTvSource, mFlContent).genMask()));

      mTipTempName.findViewById(R.id.ll_tip).setBackgroundDrawable(
          new RoundRectDrawable(U.dp2px(8), Color.WHITE));

      mFlContent.addView(mTipTempName);

      mTipTempName.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          view.setVisibility(GONE);
          U.getBus().post(new FeedRegionAdapter.DismissTipOverlayEvent(TYPE_TEMP_NAME));
          Env.setFirstRun("overlay_tip_temp_name", false);
        }
      });
    } else {
      mTipTempName.setVisibility(VISIBLE);
    }
  }

  public void hideTempNameTip() {
    hideTip(mTipTempName);
  }

  public void showTagsTip() {
    if (mTipTags == null) {
      mTipTags = LayoutInflater.from(getContext())
          .inflate(R.layout.overlay_tip_tags, this, false);

      mTipTags.setBackgroundDrawable(new BitmapDrawable(getResources(),
          new ViewHighlighter(mLlTags, mFlContent).genMask()));

      mTipTags.findViewById(R.id.ll_tip).setBackgroundDrawable(
          new RoundRectDrawable(U.dp2px(8), Color.WHITE));

      mFlContent.addView(mTipTags);

      mTipTags.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          view.setVisibility(GONE);
          U.getBus().post(new FeedRegionAdapter.DismissTipOverlayEvent(TYPE_TAGS));
          Env.setFirstRun("overlay_tip_tags", false);
        }
      });
    } else {
      mTipTags.setVisibility(VISIBLE);
    }
  }

  public void hideTagsTip() {
    hideTip(mTipTags);
  }

  private void hideTip(View view) {
    if (view != null) {
      view.setVisibility(GONE);
    }
  }
}
