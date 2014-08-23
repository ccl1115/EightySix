package com.utree.eightysix.app.msg;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;

/**
 * @author simon
 */
public class BaseMsgItemView extends LinearLayout {

  @InjectView (R.id.fl_left)
  public FrameLayout mFlLeft;

  @InjectView (R.id.fl_right)
  public FrameLayout mFlRight;

  @InjectView (R.id.tv_content_left)
  public TextView mTvContentLeft;

  @InjectView (R.id.tv_content_right)
  public TextView mTvContentRight;

  @InjectView (R.id.aiv_bg_left)
  public AsyncImageView mAivBgLeft;

  @InjectView (R.id.aiv_bg_right)
  public AsyncImageView mAivBgRight;

  @InjectView (R.id.v_mask_left)
  public View mVMaskLeft;

  @InjectView (R.id.v_mask_right)
  public View mVMaskRight;

  @InjectView(R.id.tv_count_left)
  public TextView mTvCountLeft;

  @InjectView(R.id.tv_count_right)
  public TextView mTvCountRight;

  @OnClick (R.id.fl_left)
  public void onFlLeftClicked(View view) {
    mPosts[0].read = 1;
    mPosts[0].comments = 0;
    ((BaseAdapter) ((ListView) getParent()).getAdapter()).notifyDataSetChanged();
    PostActivity.start(view.getContext(), mPosts[0]);
    ReadMsgStore.inst().addRead(mPosts[0].id);
  }

  @OnClick(R.id.fl_right)
  public void onFlRightClicked(View view) {
    mPosts[1].read = 1;
    mPosts[1].comments = 0;
    ((BaseAdapter) ((ListView) getParent()).getAdapter()).notifyDataSetChanged();
    PostActivity.start(view.getContext(), mPosts[1]);
    ReadMsgStore.inst().addRead(mPosts[1].id);
  }

  protected Post[] mPosts;

  public BaseMsgItemView(Context context) {
    this(context, null);
  }

  public BaseMsgItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.item_msg, this);
    U.viewBinding(this, this);

    M.getRegisterHelper().register(this);
  }

  public void setData(Post[] posts) {
    mPosts = posts;
    if (posts.length == 2) {
      setLeftData(posts[0]);
      setRightData(posts[1]);
    }
  }

  protected void setLeftThemedColor(int color) {
    int monoColor = ColorUtil.monochromizing(color);
    mTvContentLeft.setTextColor(monoColor);
    mTvCountLeft.setTextColor(monoColor);
  }

  protected void setRightThemedColor(int color) {
    int monoColor = ColorUtil.monochromizing(color);
    mTvContentRight.setTextColor(monoColor);
    mTvCountRight.setTextColor(monoColor);
  }

  protected void setRightData(Post right) {
    if (right != null) {

      if (TextUtils.isEmpty(right.bgUrl)) {
        setRightThemedColor(ColorUtil.strToColor(right.bgColor));
      //} else {
      //  Bitmap fromMemByUrl = ImageUtils.getFromMemByUrl(right.bgUrl);
      //  if (fromMemByUrl != null) {
      //    ColorUtil.asyncThemedColor(fromMemByUrl);
      //  }
      }
      mFlRight.setVisibility(VISIBLE);
      mTvContentRight.setText(right.content);
      if (!TextUtils.isEmpty(right.bgUrl)) {
        mAivBgRight.setUrl(right.bgUrl);
        mTvContentRight.setBackgroundColor(Color.TRANSPARENT);
      } else {
        mAivBgRight.setUrl(null);
        mTvContentRight.setBackgroundColor(ColorUtil.strToColor(right.bgColor));
      }
    } else {
      mFlRight.setVisibility(INVISIBLE);
    }
  }

  protected void setLeftData(Post left) {
    if (left != null) {

      if (TextUtils.isEmpty(left.bgUrl)) {
        setLeftThemedColor(ColorUtil.strToColor(left.bgColor));
      }

      mFlLeft.setVisibility(VISIBLE);
      mTvContentLeft.setText(left.content);
      if (!TextUtils.isEmpty(left.bgUrl)) {
        mAivBgLeft.setUrl(left.bgUrl);
        mTvContentLeft.setBackgroundColor(Color.TRANSPARENT);
      } else {
        mAivBgLeft.setUrl(null);
        mTvContentLeft.setBackgroundColor(ColorUtil.strToColor(left.bgColor));
      }
    } else {
      mFlLeft.setVisibility(INVISIBLE);
    }

  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    super.onMeasure(widthMeasureSpec, (widthSize >> 1) + MeasureSpec.EXACTLY);
  }

  @Override
  protected void onDetachedFromWindow() {
    M.getRegisterHelper().unregister(this);
    super.onDetachedFromWindow();
  }
}
