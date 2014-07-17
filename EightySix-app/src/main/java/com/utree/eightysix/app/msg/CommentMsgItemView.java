package com.utree.eightysix.app.msg;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ColorUtil;

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

    if (left != null) {
      mTvCountLeft.setText("+ " + String.valueOf(left.praise));
    }
  }

  @Override
  protected void setRightData(Post right) {
    super.setRightData(right);

    if (right != null) {
      mTvCountRight.setText("+ " + String.valueOf(right.praise));
    }
  }

  @Override
  protected void setLeftThemedColor(int color) {
    super.setLeftThemedColor(color);

    int monoColor = ColorUtil.monochromizing(color);
    if (monoColor == Color.WHITE) {
      mTvCountLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply, 0, 0, 0);
    } else {
      mTvCountLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_black_reply, 0, 0, 0);
    }
    mTvCountLeft.setCompoundDrawablePadding(U.dp2px(8));

    invalidate();
  }

  @Override
  protected void setRightThemedColor(int color) {
    super.setRightThemedColor(color);

    int monoColor = ColorUtil.monochromizing(color);
    if (monoColor == Color.WHITE) {
      mTvCountRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply, 0, 0, 0);
    } else {
      mTvCountRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_black_reply, 0, 0, 0);
    }
    mTvCountRight.setCompoundDrawablePadding(U.dp2px(8));

    invalidate();
  }
}
