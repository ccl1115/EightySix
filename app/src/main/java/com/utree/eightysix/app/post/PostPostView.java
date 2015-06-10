package com.utree.eightysix.app.post;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.utree.eightysix.app.feed.FeedsSearchActivity;
import com.utree.eightysix.app.feed.event.PostPostPraiseEvent;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.request.PostDeleteRequest;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TagView;

import java.util.List;

import static com.utree.eightysix.utils.TextUtils.*;

/**
 * This is the post view in PostActivity
 *
 * @author simon
 * @see PostActivity
 */
public class PostPostView extends LinearLayout {

  private static int sPostLength = U.getConfigInt("post.length");

  @InjectView(R.id.aiv_bg)
  public AsyncImageView mAivBg;

  @InjectView(R.id.vp_content)
  public ViewPager mVpContent;

  @InjectView(R.id.tv_source)
  public TextView mTvSource;

  @InjectView(R.id.tv_comment)
  public TextView mTvComment;

  @InjectView(R.id.tv_praise)
  public TextView mTvPraise;

  @InjectView(R.id.tv_distance)
  public TextView mTvDistance;

  @InjectView(R.id.tv_tag_1)
  public TagView mTvTag1;

  @InjectView(R.id.tv_tag_2)
  public TagView mTvTag2;

  @InjectView(R.id.rb_page)
  public RoundedButton mRbPage;

  private Post mPost;

  private boolean mClicked = false;

  private Runnable mCancel = new Runnable() {
    @Override
    public void run() {
      mClicked = false;
    }
  };

  public PostPostView(Context context) {
    this(context, null);
  }

  public PostPostView(Context context, AttributeSet attrs) {
    super(context, attrs);

    LayoutInflater.from(context).inflate(R.layout.item_post_post, this);
    ButterKnife.inject(this, this);

    M.getRegisterHelper().register(this);

    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mClicked) {
          mClicked = false;
          mVpContent.setVisibility(mVpContent.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
          removeCallbacks(mCancel);
        } else {
          mClicked = true;
          postDelayed(mCancel, 1000);
        }
      }
    });

  }

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

  @OnClick(R.id.iv_more)
  public void onIvMoreClicked() {
    if (mPost == null) return;

    U.getAnalyser().trackEvent(U.getContext(), "post_more", "post_more");
    String[] items;
    if (mPost.owner == 1) {
      items = new String[]{
          getResources().getString(R.string.chat_anonymous),
          getResources().getString(R.string.share),
          getResources().getString(R.string.report),
          getResources().getString(R.string.delete)};
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

  public void setData(Post post) {
    mPost = post;

    if (mPost == null) {
      return;
    }

    if (!TextUtils.isEmpty(mPost.userName)) {
      setPadding(0, U.dp2px(46), 0, 0);
    } else {
      setPadding(0, 0, 0, 0);
    }

    int size = getResources().getDisplayMetrics().widthPixels - 2 * U.dp2px(48);
    final List<CharSequence> paged = page(mPost.content, size, size - U.dp2px(46), 23);

    mVpContent.setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return paged.size();
      }

      @Override
      public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
      }

      @Override
      public Object instantiateItem(ViewGroup container, int position) {
        TextView view = new TextView(container.getContext());
        view.setTextSize(23);
        view.setPadding(U.dp2px(48), 0, U.dp2px(48), 0);
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        view.setText(paged.get(position));
        view.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mClicked) {
              mClicked = false;
              mVpContent.setVisibility(mVpContent.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
              removeCallbacks(mCancel);
            } else {
              mClicked = true;
              postDelayed(mCancel, 1000);
            }
          }
        });
        container.addView(view);
        return view;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
      }
    });

    if (paged.size() > 1) {
      mRbPage.setVisibility(VISIBLE);
      mRbPage.setText(String.format("%d/%d", mVpContent.getCurrentItem() + 1, paged.size()));

      mVpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
          mRbPage.setText(String.format("%d/%d", mVpContent.getCurrentItem() + 1, paged.size()));
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
      });
    } else {
      mRbPage.setVisibility(GONE);
    }

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

    mTvDistance.setText(mPost.distance);

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

  @OnClick(R.id.tv_praise)
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
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    removeCallbacks(mCancel);
  }

  @Override
  protected void onDetachedFromWindow() {
    M.getRegisterHelper().unregister(this);
    removeCallbacks(mCancel);
    super.onDetachedFromWindow();
  }
}
