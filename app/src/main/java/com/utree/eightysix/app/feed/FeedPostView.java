package com.utree.eightysix.app.feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.chat.ChatUtils;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.app.post.ReportDialog;
import com.utree.eightysix.app.region.FeedRegionAdapter;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.PostDeleteRequest;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.ViewHighlighter;

import java.util.List;

import static com.utree.eightysix.app.region.FeedRegionAdapter.DismissTipOverlayEvent.*;

/**
 */
public class FeedPostView extends LinearLayout {

  private static int sPostLength = U.getConfigInt("post.length");

  @InjectView(R.id.tv_content)
  public TextView mTvContent;

  @InjectView(R.id.tv_source)
  public TextView mTvSource;

  @InjectView(R.id.tv_praise)
  public TextView mTvPraise;

  @InjectView(R.id.tv_comment)
  public TextView mTvComment;

  @InjectView(R.id.tv_last_comment)
  public TextView mTvLastComment;

  @InjectView(R.id.tv_last_comment_head)
  public TextView mTvLastCommentHead;

  @InjectView(R.id.tv_last_comment_tail)
  public TextView mTvLastCommentTail;

  @InjectView(R.id.aiv_bg)
  public AsyncImageView mAivBg;

  @InjectView(R.id.ll_comment)
  public LinearLayout mLlComment;

  @InjectView(R.id.fl_content)
  public FrameLayout mFlContent;

  @InjectView(R.id.tv_tag_1)
  public TextView mTvTag1;

  @InjectView(R.id.tv_tag_2)
  public TextView mTvTag2;

  @InjectView(R.id.ll_tags)
  public LinearLayout mLlTags;

  @InjectView(R.id.tv_hometown)
  public TextView mTvHometown;

  @InjectView(R.id.tv_distance)
  public TextView mTvDistance;

  @InjectView(R.id.tv_name)
  public TextView mTvName;

  @InjectView(R.id.aiv_portrait)
  public AsyncImageViewWithRoundCorner mAivPortrait;

  @InjectView(R.id.ll_top)
  public LinearLayout mLlTop;

  @InjectView(R.id.aiv_level_icon)
  public AsyncImageView mAivLevelIcon;

  private Post mPost;

  private final Runnable mTagAnimation;

  private View mTipSource;
  private View mTipPraise;
  private View mTipRepost;
  private View mTipTempName;
  private View mTipTags;

  @OnClick(R.id.tv_tag_1)
  public void onTvTag1Clicked() {
    FeedsSearchActivity.start(getContext(), mPost.tags.get(0).content);
  }

  @OnClick(R.id.tv_tag_2)
  public void onTvTag2Clicked() {
    FeedsSearchActivity.start(getContext(), mPost.tags.get(1).content);
  }

  @OnClick(R.id.tv_source)
  public void onTvSourceClicked() {
    if (mPost.jump == 1) {
      FeedActivity.start(getContext(), mPost.factoryId);
    }
  }

  @OnClick(R.id.ll_top)
  public void onLlTopClicked() {
    if (!TextUtils.isEmpty(mPost.viewUserId)) {
      Bundle args = new Bundle();
      args.putInt("viewId", Integer.valueOf(mPost.viewUserId));
      args.putBoolean("isVisitor", true);
      args.putString("userName", mPost.userName);
      FragmentHolder.start(getContext(), ProfileFragment.class, args);
    }
  }

  @OnClick(R.id.iv_more)
  public void onIvMoreClicked() {
    String[] items;
    if (mPost.owner == 1) {
      items = new String[]{
          getResources().getString(R.string.chat_anonymous),
          getResources().getString(R.string.share),
          getResources().getString(R.string.report),
          getResources().getString(R.string.delete),
      };
    } else {
      items = new String[]{
          getResources().getString(R.string.chat_anonymous),
          getResources().getString(R.string.share),
          getResources().getString(R.string.report),
      };
    }
    new AlertDialog.Builder(getContext()).setTitle(U.gs(R.string.post_action))
        .setItems(items,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    ChatUtils.startChat(((BaseActivity) getContext()), mPost);
                    break;
                  case 1:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_share", "post_more_share");
                    U.getShareManager().sharePostDialog(((BaseActivity) getContext()), mPost).show();
                    break;
                  case 2:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_report", "post_more_report");
                    new ReportDialog(getContext(), mPost.id).show();
                    break;
                  case 3:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_delete", "post_more_delete");
                    U.getBus().post(new PostDeleteRequest(mPost.id));
                    break;
                }
              }
            }).create().show();
  }

  public FeedPostView(Context context) {
    this(context, null);
  }

  public FeedPostView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.item_feed_post, this);

    setOrientation(VERTICAL);
    setPadding(0, U.dp2px(3), 0, 0);

    U.viewBinding(this, this);

    M.getRegisterHelper().register(this);

    mTagAnimation = new Runnable() {

      @Override
      public void run() {
        mTvTag1.setVisibility(VISIBLE);
        mTvTag2.setVisibility(VISIBLE);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(mTvTag1, "alpha", 0, 1f);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(mTvTag2, "alpha", 0, 1f);
        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(mTvTag1, "scaleX", 0.8f, 1.1f, 0.9f, 1.0f);
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(mTvTag2, "scaleX", 0.8f, 1.1f, 0.9f, 1.0f);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(mTvTag1, "scaleY", 0.8f, 1.1f, 0.9f, 1.0f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(mTvTag2, "scaleY", 0.8f, 1.1f, 0.9f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alpha1, alpha2, scaleX1, scaleX2, scaleY1, scaleY2);
        set.setDuration(500);
        set.start();
      }
    };
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
    mTvLastComment.setText(post.comment);
    mTvLastCommentHead.setText(post.commentHead);
    mTvLastCommentTail.setText(post.commentTail);

    if (mPost.isRepost == 1) {
      mTvSource.setText("转自" + mPost.source);
    } else {
      mTvSource.setText(mPost.source);
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
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white_normal, 0, 0, 0);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_outline_normal, 0, 0, 0);
    }

    mTvComment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply, 0, 0, 0);

    if (TextUtils.isEmpty(post.comment)) {
      mLlComment.setVisibility(GONE);
    } else {
      mLlComment.setVisibility(VISIBLE);
    }

    mTvTag1.setText("");
    mTvTag2.setText("");

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
        }
      }
    }

    if (TextUtils.isEmpty(post.hometownText)) {
      mTvHometown.setVisibility(GONE);
    } else {
      mTvHometown.setVisibility(VISIBLE);
      mTvHometown.setText(post.hometownText);
    }

    if (!TextUtils.isEmpty(mPost.userName)) {
      mLlTop.setVisibility(VISIBLE);
      mTvName.setText(mPost.userName);
      mAivPortrait.setUrl(mPost.avatar);
    } else {
      mLlTop.setVisibility(GONE);
    }

    mTvDistance.setText(mPost.distance);

    if (!TextUtils.isEmpty(mPost.levelIcon)) {
      mAivLevelIcon.setUrl(mPost.levelIcon);
    }

    mTvTag1.setVisibility(INVISIBLE);
    mTvTag2.setVisibility(INVISIBLE);
    removeCallbacks(mTagAnimation);
    postDelayed(mTagAnimation, 500);
  }

  @OnClick(R.id.tv_praise)
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
      ((BaseAdapter) ((AdapterView) getParent()).getAdapter()).notifyDataSetChanged();
    }
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
