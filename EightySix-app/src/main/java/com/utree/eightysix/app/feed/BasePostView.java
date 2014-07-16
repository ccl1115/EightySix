package com.utree.eightysix.app.feed;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.utree.eightysix.R;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ColorUtil;

/**
 * @author simon
 */
public class BasePostView extends FrameLayout {
  protected Post mPost;

  protected int mHeartRes;
  protected int mHeartOutlineRes;
  protected int mCommentRes;
  protected int mMonoColor;

  public BasePostView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
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
