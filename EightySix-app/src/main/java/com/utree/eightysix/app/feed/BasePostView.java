package com.utree.eightysix.app.feed;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.utree.eightysix.R;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ColorUtil;

/**
 * @author simon
 */
public class BasePostView extends LinearLayout {
  protected Post mPost;

  protected int mHeartRes = R.drawable.ic_black_heart_white_normal;
  protected int mHeartOutlineRes = R.drawable.ic_black_heart_outline_normal;
  protected int mCommentRes = R.drawable.ic_black_reply;
  protected int mMonoColor;

  public BasePostView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  protected void setPostTheme(int color) {
    mMonoColor = ColorUtil.monochromizing(color);

    if (mMonoColor == Color.WHITE) {
      mHeartRes = R.drawable.ic_heart_white_normal;
      mHeartOutlineRes = R.drawable.ic_heart_outline_normal;
      mCommentRes = R.drawable.ic_reply;
    } else {
      mHeartRes = R.drawable.ic_black_heart_white_normal;
      mHeartOutlineRes = R.drawable.ic_black_heart_outline_normal;
      mCommentRes = R.drawable.ic_black_reply;
    }
  }
}
