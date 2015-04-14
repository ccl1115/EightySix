package com.utree.eightysix.app.post;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.chat.ChatUtils;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.feed.event.PostPostPraiseEvent;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.app.tag.TagTabActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.request.PostDeleteRequest;
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
public class PostPostView extends LinearLayout {

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

  private Post mPost;

  public PostPostView(Context context) {
    this(context, null);
  }

  public PostPostView(Context context, AttributeSet attrs) {
    super(context, attrs);

    LayoutInflater.from(context).inflate(R.layout.item_post_post, this);
    ButterKnife.inject(this, this);

    M.getRegisterHelper().register(this);
  }

  @OnClick (R.id.tv_tag_1)
  public void onTvTag1Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(0));
  }

  @OnClick (R.id.tv_tag_2)
  public void onTvTag2Clicked() {
    TagTabActivity.start(getContext(), mPost.tags.get(1));
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

  @OnClick(R.id.iv_more)
  public void onIvMoreClicked() {
    if (mPost == null) return;
    
    U.getAnalyser().trackEvent(U.getContext(), "post_more", "post_more");
    String[] items;
    if (mPost.owner == 1) {
      items = new String[]{U.gs(R.string.share),
          getResources().getString(R.string.start_chat),
          getResources().getString(R.string.report),
          getResources().getString(R.string.like),
          getResources().getString(R.string.delete)};
    } else {
      items = new String[]{U.gs(R.string.share),
          getResources().getString(R.string.start_chat),
          getResources().getString(R.string.report),
          getResources().getString(R.string.like)};
    }
    new AlertDialog.Builder(getContext()).setTitle(U.gs(R.string.post_action))
        .setItems(items,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_share", "post_more_share");
                    U.getShareManager().sharePostDialog(((BaseActivity) getContext()), mPost).show();
                    break;
                  case 1:
                    ChatUtils.startChat(((BaseActivity) getContext()), mPost);
                    break;
                  case 2:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_report", "post_more_report");
                    new ReportDialog(getContext(), mPost.id).show();
                    break;
                  case 3:
                    if (mPost == null) return;
                    if (mPost.praised != 1) {
                      U.getAnalyser().trackEvent(U.getContext(), "post_more_praise", "praise");
                      doPraise();
                    }
                    break;
                  case 4:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_delete", "post_more_delete");
                    U.getBus().post(new PostDeleteRequest(mPost.id));
                    break;
                }
              }
            }).create().show();

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

    mTvComment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply, 0, 0, 0);

    if (mPost.praised == 1) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_pressed, 0, 0, 0);
    } else if (mPost.praise > 0) {
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white_normal, 0, 0, 0);
    } else {
      mTvPraise.setText("");
      mTvPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_outline_normal, 0, 0, 0);
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
  protected void onDetachedFromWindow() {
    M.getRegisterHelper().unregister(this);
    super.onDetachedFromWindow();
  }
}
