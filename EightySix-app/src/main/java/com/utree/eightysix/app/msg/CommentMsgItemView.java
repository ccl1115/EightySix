package com.utree.eightysix.app.msg;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.ImageUtils;

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

    if (right != null) {
      if (right.comments == 0) {
        mTvCountRight.setText("");
      } else {
        mTvCountRight.setText("+ " + String.valueOf(right.comments));
      }
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

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (mPosts != null && mPosts.length == 2) {
      if (mPosts[0] != null) {
        if (!TextUtils.isEmpty(mPosts[0].bgUrl)) {
          if (event.getHash().equals(ImageUtils.getUrlHash(mPosts[0].bgUrl))) {
            ColorUtil.asyncThemedColor(event.getBitmap());
          }
        }
      }

      if (mPosts[1] != null) {
        if (!TextUtils.isEmpty(mPosts[1].bgUrl)) {
          if (event.getHash().equals(ImageUtils.getUrlHash(mPosts[1].bgUrl))) {
            ColorUtil.asyncThemedColor(event.getBitmap());
          }
        }
      }
    }
  }

  @Subscribe
  public void onThemedColorEvent(ColorUtil.ThemedColorEvent event) {
    if (mPosts != null && mPosts.length == 2) {
      if (mPosts[0] != null && !TextUtils.isEmpty(mPosts[0].bgUrl)) {
        if (event.getBitmap().equals(ImageUtils.getFromMemByUrl(mPosts[0].bgUrl))) {
          setLeftThemedColor(event.getColor());
        }
      }

      if (mPosts[1] != null && !TextUtils.isEmpty(mPosts[1].bgUrl)) {
        if (event.getBitmap().equals(ImageUtils.getFromMemByUrl(mPosts[1].bgUrl))) {
          setRightThemedColor(event.getColor());
        }
      }
    }
  }
}
