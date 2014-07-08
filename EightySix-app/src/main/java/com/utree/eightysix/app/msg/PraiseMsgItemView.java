package com.utree.eightysix.app.msg;

import android.content.Context;
import android.util.AttributeSet;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
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

    if (left != null) {
      mTvCountLeft.setText(String.valueOf(left.praise));
      mTvCountLeft.setCompoundDrawablePadding(U.dp2px(8));
      mTvCountLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white_normal, 0, 0, 0);
    }
  }

  @Override
  protected void setRightData(Post right) {
    super.setRightData(right);

    if (right != null) {
      mTvCountRight.setText(String.valueOf(right.praise));
      mTvCountRight.setCompoundDrawablePadding(U.dp2px(8));
      mTvCountRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white_normal, 0, 0, 0);
    }
  }
}
