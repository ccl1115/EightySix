package com.utree.eightysix.app.msg;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.AsyncImageView;

/**
 * @author simon
 */
public class MsgItemView extends LinearLayout {

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

  @OnClick (R.id.fl_left)
  public void onFlLeftClicked() {
    U.getBus().post(mPosts[0]);
  }

  @OnClick(R.id.fl_right)
  public void onFlRightClicked() {
    U.getBus().post(mPosts[1]);
  }

  private Post[] mPosts;

  public MsgItemView(Context context) {
    this(context, null);
  }

  public MsgItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.item_msg, this);
    U.viewBinding(this, this);
  }

  public void setData(Post[] posts) {
    mPosts = posts;
    if (posts.length == 2) {
      final Post left = posts[0];
      if (left != null) {
        mFlLeft.setVisibility(VISIBLE);
        mTvContentLeft.setText(left.content);
        if (!TextUtils.isEmpty(left.bgUrl)) {
          mAivBgLeft.setUrl(left.bgUrl);
          mTvContentLeft.setBackgroundColor(Color.TRANSPARENT);
        } else {
          mAivBgLeft.setUrl(null);
          mTvContentLeft.setBackgroundColor(left.bgColor);
        }
        if (left.read == 1) {
          mVMaskLeft.setVisibility(VISIBLE);
        } else {
          mVMaskLeft.setVisibility(INVISIBLE);
        }
      } else {
        mFlLeft.setVisibility(INVISIBLE);
      }

      final Post right = posts[1];
      if (right != null) {
        mFlRight.setVisibility(VISIBLE);
        mTvContentRight.setText(right.content);
        if (!TextUtils.isEmpty(right.bgUrl)) {
          mAivBgRight.setUrl(right.bgUrl);
          mTvContentRight.setBackgroundColor(Color.TRANSPARENT);
        } else {
          mAivBgRight.setUrl(null);
          mTvContentRight.setBackgroundColor(right.bgColor);
        }
        if (right.read == 1) {
          mVMaskRight.setVisibility(VISIBLE);
        } else {
          mVMaskRight.setVisibility(INVISIBLE);
        }
      } else {
        mFlRight.setVisibility(INVISIBLE);
      }
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    super.onMeasure(widthMeasureSpec, (widthSize >> 1) + MeasureSpec.EXACTLY);
  }
}
