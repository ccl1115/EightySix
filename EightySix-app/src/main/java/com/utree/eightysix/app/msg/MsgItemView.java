package com.utree.eightysix.app.msg;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.AsyncImageView;
import java.awt.TexturePaint;

/**
 * @author simon
 */
public class MsgItemView extends LinearLayout {

  @InjectView (R.id.tv_content_left)
  public TextView mTvContentLeft;

  @InjectView (R.id.tv_content_right)
  public TextView mTvContentRight;

  @InjectView (R.id.aiv_bg_left)
  public AsyncImageView mAivBgLeft;

  @InjectView (R.id.aiv_bg_right)
  public AsyncImageView mAivBgRight;

  @InjectView (R.id.iv_more_left)
  public ImageView mIvMoreLeft;

  @InjectView (R.id.iv_more_right)
  public ImageView mIvMoreRight;

  public MsgItemView(Context context) {
    this(context, null);
  }

  public MsgItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.item_msg, this);
    U.viewBinding(this, this);
  }

  public void setData(Post[] posts) {
    if (posts.length == 2) {
      Post left = posts[0];
      if (left != null) {
        mTvContentLeft.setText(left.content);
        if (!TextUtils.isEmpty(left.bgUrl)) {
          mAivBgLeft.setUrl(left.bgUrl);
          mTvContentLeft.setBackgroundColor(Color.TRANSPARENT);
        } else {
          mAivBgLeft.setUrl(null);
          mTvContentLeft.setBackgroundColor(left.bgColor);
        }
      }

      Post right = posts[1];
      if (right != null) {
        mTvContentRight.setText(right.content);
        if (!TextUtils.isEmpty(right.bgUrl)) {
          mAivBgRight.setUrl(right.bgUrl);
          mTvContentRight.setBackgroundColor(Color.TRANSPARENT);
        } else {
          mAivBgRight.setUrl(null);
          mTvContentRight.setBackgroundColor(right.bgColor);
        }
      }
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    super.onMeasure(widthMeasureSpec, (widthSize >> 1) + MeasureSpec.EXACTLY);
  }
}
