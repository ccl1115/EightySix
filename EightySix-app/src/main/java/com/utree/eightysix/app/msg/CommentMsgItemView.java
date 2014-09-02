package com.utree.eightysix.app.msg;

import android.content.Context;
import android.util.AttributeSet;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class CommentMsgItemView extends BaseMsgItemView {
  public CommentMsgItemView(Context context) {
    this(context, null);
  }

  public CommentMsgItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void setLeftData(Post left) {
    super.setLeftData(left);

    if (left == null) return;

    if (left.read == 1 || ReadMsgStore.inst().isRead(left.id)) {
      mVMaskLeft.setVisibility(VISIBLE);
    } else {
      mVMaskLeft.setVisibility(INVISIBLE);
      if (left.comments == 0) {
        mTvCountLeft.setText("");
      } else {
        mTvCountLeft.setText("+ " + String.valueOf(left.comments));
      }
    }

  }

  @Override
  protected void setRightData(Post right) {
    super.setRightData(right);

    if (right == null) return;

    if (right.read == 1 || ReadMsgStore.inst().isRead(right.id)) {
      mVMaskRight.setVisibility(VISIBLE);
    } else {
      mVMaskRight.setVisibility(INVISIBLE);
      if (right.comments == 0) {
        mTvCountRight.setText("");
      } else {
        mTvCountRight.setText("+ " + String.valueOf(right.comments));
      }
    }

  }
}
