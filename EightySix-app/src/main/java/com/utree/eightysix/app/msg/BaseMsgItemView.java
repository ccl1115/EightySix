package com.utree.eightysix.app.msg;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.Utils;
import com.utree.eightysix.widget.AsyncImageView;

/**
 * @author simon
 */
public class BaseMsgItemView extends LinearLayout {

  @InjectView (R.id.fl_left)
  public FrameLayout mFlLeft;

  @InjectView (R.id.fl_right)
  public FrameLayout mFlRight;

  @InjectView (R.id.tv_content_left)
  public TextView mTvContentLeft;

  @InjectView (R.id.tv_content_right)
  public TextView mTvContentRight;

  @InjectView (R.id.aiv_bg_left)
  public AsyncImageView mAivBgLeft;

  @InjectView (R.id.aiv_bg_right)
  public AsyncImageView mAivBgRight;

  @InjectView (R.id.v_mask_left)
  public View mVMaskLeft;

  @InjectView (R.id.v_mask_right)
  public View mVMaskRight;

  @InjectView(R.id.tv_count_left)
  public TextView mTvCountLeft;

  @InjectView(R.id.tv_count_right)
  public TextView mTvCountRight;

  @OnClick (R.id.fl_left)
  public void onFlLeftClicked(View view) {
    PostActivity.start(view.getContext(), mPosts[0], null);
  }

  @OnClick(R.id.fl_right)
  public void onFlRightClicked(View view) {
    PostActivity.start(view.getContext(), mPosts[1], null);
  }

  private Post[] mPosts;

  public BaseMsgItemView(Context context) {
    this(context, null);
  }

  public BaseMsgItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.item_msg, this);
    U.viewBinding(this, this);
  }

  public void setData(Post[] posts) {
    mPosts = posts;
    if (posts.length == 2) {
      setLeftData(posts[0]);
      setRightData(posts[1]);
    }
  }

  protected void setRightData(Post right) {
    if (right != null) {
      mFlRight.setVisibility(VISIBLE);
      mTvContentRight.setText(right.content);
      if (!TextUtils.isEmpty(right.bgUrl)) {
        mAivBgRight.setUrl(right.bgUrl);
        mTvContentRight.setBackgroundColor(Color.TRANSPARENT);
      } else {
        mAivBgRight.setUrl(null);
        final int color = Utils.strToColor(right.bgColor);
        mTvContentRight.setBackgroundColor(color);
        mTvContentRight.setTextColor(Utils.monochromizing(color));
      }
      if (right.read == 1) {
        mVMaskRight.setVisibility(INVISIBLE);
      } else {
        mVMaskRight.setVisibility(VISIBLE);
      }
    } else {
      mFlRight.setVisibility(INVISIBLE);
    }
  }

  protected void setLeftData(Post left) {
    if (left != null) {
      mFlLeft.setVisibility(VISIBLE);
      mTvContentLeft.setText(left.content);
      if (!TextUtils.isEmpty(left.bgUrl)) {
        mAivBgLeft.setUrl(left.bgUrl);
        mTvContentLeft.setBackgroundColor(Color.TRANSPARENT);
      } else {
        mAivBgLeft.setUrl(null);
        final int color = Utils.strToColor(left.bgColor);
        mTvContentLeft.setBackgroundColor(color);
        mTvContentLeft.setTextColor(Utils.monochromizing(color));
      }
      if (left.read == 1) {
        mVMaskLeft.setVisibility(INVISIBLE);
      } else {
        mVMaskLeft.setVisibility(VISIBLE);
      }
    } else {
      mFlLeft.setVisibility(INVISIBLE);
    }

  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    super.onMeasure(widthMeasureSpec, (widthSize >> 1) + MeasureSpec.EXACTLY);
  }
}
