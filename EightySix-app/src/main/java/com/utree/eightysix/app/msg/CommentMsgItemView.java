package com.utree.eightysix.app.msg;

import android.content.Context;
import android.util.AttributeSet;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
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

    if (left != null) {
      mTvCountLeft.setText(String.valueOf(left.praise));
      mTvCountLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply, 0, 0, 0);
      mTvCountLeft.setCompoundDrawablePadding(U.dp2px(8));
    }
  }

  @Override
  protected void setRightData(Post right) {
    super.setRightData(right);

    if (right != null) {
      mTvCountRight.setText(String.valueOf(right.praise));
      mTvCountRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply, 0, 0, 0);
      mTvCountRight.setCompoundDrawablePadding(U.dp2px(8));
    }
  }
}
