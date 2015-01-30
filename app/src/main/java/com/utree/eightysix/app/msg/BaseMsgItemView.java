package com.utree.eightysix.app.msg;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.CancelNoticeRequest;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.ThemedDialog;

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

  @InjectView(R.id.iv_unfollow_left)
  public ImageView mIvUnfollowLeft;

  @InjectView(R.id.iv_unfollow_right)
  public ImageView mIvUnfollowRight;

  @OnClick(R.id.iv_unfollow_left)
  public void onIvUnfollowLeftClicked() {
    if (mPosts[0].relation == 1) {
      showUnfollowDialog(mPosts[0]);
    }
  }

  @OnClick(R.id.iv_unfollow_right)
  public void onIvUnfollowRightClicked() {
    if (mPosts[1].relation == 1) {
      showUnfollowDialog(mPosts[1]);
    }
  }

  @OnClick (R.id.fl_left)
  public void onFlLeftClicked(View view) {
    mPosts[0].read = 1;
    mPosts[0].comments = 0;
    ((BaseAdapter) ((ListView) getParent()).getAdapter()).notifyDataSetChanged();
    PostActivity.start(view.getContext(), mPosts[0], true);
    ReadMsgStore.inst().addRead(mPosts[0].id);
  }

  @OnClick(R.id.fl_right)
  public void onFlRightClicked(View view) {
    mPosts[1].read = 1;
    mPosts[1].comments = 0;
    ((BaseAdapter) ((ListView) getParent()).getAdapter()).notifyDataSetChanged();
    PostActivity.start(view.getContext(), mPosts[1], true);
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

  protected void setRightData(Post right) {
    if (right != null) {
      mFlRight.setVisibility(VISIBLE);
      mTvContentRight.setText(right.content);
      if (!TextUtils.isEmpty(right.bgUrl)) {
        mAivBgRight.setUrl(right.bgUrl, 300, 300);
        mTvContentRight.setBackgroundColor(Color.TRANSPARENT);
      } else {
        mAivBgRight.setUrl(null);
        mTvContentRight.setBackgroundColor(ColorUtil.strToColor(right.bgColor));
      }

      if (right.relation == 0) {
        mIvUnfollowRight.setImageResource(R.drawable.ic_unfollow_pressed);
      } else {
        mIvUnfollowRight.setImageResource(R.drawable.ic_unfollow_normal);
      }
    } else {
      mFlRight.setVisibility(INVISIBLE);
    }
  }

  protected void setLeftData(Post left) {
    if (left != null) {
      mFlLeft.setVisibility(VISIBLE);
      mTvContentLeft.setText(left.content);
      if (!TextUtils.isEmpty(left.bgUrl)) {
        mAivBgLeft.setUrl(left.bgUrl, 300, 300);
        mTvContentLeft.setBackgroundColor(Color.TRANSPARENT);
      } else {
        mAivBgLeft.setUrl(null);
        mTvContentLeft.setBackgroundColor(ColorUtil.strToColor(left.bgColor));
      }

      if (left.relation == 0) {
        mIvUnfollowLeft.setImageResource(R.drawable.ic_unfollow_pressed);
      } else {
        mIvUnfollowLeft.setImageResource(R.drawable.ic_unfollow_normal);
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

  public void showUnfollowDialog(final Post post) {
    final ThemedDialog dialog = new ThemedDialog(getContext());

    dialog.setTitle("确认不再接收此帖回复消息？");

    dialog.setPositive("确认", new OnClickListener() {
      @Override
      public void onClick(View view) {
        U.getRESTRequester().request(new CancelNoticeRequest(post.id), new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              post.relation = 0;
              U.getBus().post(post);
            }
          }
        }, Response.class);
        dialog.dismiss();
      }
    });

    dialog.setRbNegative("取消", new OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.dismiss();
      }
    });

    dialog.show();
  }
}
