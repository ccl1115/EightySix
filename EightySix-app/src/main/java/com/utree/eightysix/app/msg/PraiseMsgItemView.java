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
  }

  @Override
  protected void setLeftThemedColor(int color) {
    super.setLeftThemedColor(color);

    int monoColor = ColorUtil.monochromizing(color);
    if (monoColor == Color.WHITE) {
      mTvCountLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white_normal, 0, 0, 0);
    } else {
      mTvCountLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_black_heart_white_normal, 0, 0, 0);
    }
    mTvCountLeft.setCompoundDrawablePadding(U.dp2px(8));

    invalidate();
  }

  @Override
  protected void setRightThemedColor(int color) {
    super.setRightThemedColor(color);

    int monoColor = ColorUtil.monochromizing(color);
    if (monoColor == Color.WHITE) {
      mTvCountRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white_normal, 0, 0, 0);
    } else {
      mTvCountRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_black_heart_white_normal, 0, 0, 0);
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
            ColorUtil.asyncThemedColor(event.getHash(), event.getBitmap());
          }
        }
      }

      if (mPosts[1] != null) {
        if (!TextUtils.isEmpty(mPosts[1].bgUrl)) {
          if (event.getHash().equals(ImageUtils.getUrlHash(mPosts[1].bgUrl))) {
            ColorUtil.asyncThemedColor(event.getHash(), event.getBitmap());
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
