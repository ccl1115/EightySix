package com.utree.eightysix.app.msg;

import android.content.Context;
import android.util.AttributeSet;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class PraiseMsgItemView extends BaseMsgItemView {
  public PraiseMsgItemView(Context context) {
    this(context, null);
  }

  public PraiseMsgItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void setLeftData(Post left) {
    super.setLeftData(left);

    if (left == null) return;

    if (left.read == 1 || ReadMsgStore.inst().isRead(left.id)) {
      left.comments = 0;
      mVMaskLeft.setVisibility(VISIBLE);
    } else {
      mVMaskLeft.setVisibility(INVISIBLE);
    }

    if (left.praise == 0) {
      mTvCountLeft.setText("");
    } else {
      mTvCountLeft.setText(String.valueOf(left.praise));
    }

    mIvUnfollowLeft.setVisibility(GONE);
  }

  @Override
  protected void setRightData(Post right) {
    super.setRightData(right);

    if (right == null) return;

    if (right.read == 1) {
      right.comments = 0;
      mVMaskRight.setVisibility(VISIBLE);
    } else {
      mVMaskRight.setVisibility(INVISIBLE);
    }

    if (right.praise == 0) {
      mTvCountRight.setText("");
    } else {
      mTvCountRight.setText(String.valueOf(right.praise));
    }

    mIvUnfollowRight.setVisibility(GONE);
  }
}
