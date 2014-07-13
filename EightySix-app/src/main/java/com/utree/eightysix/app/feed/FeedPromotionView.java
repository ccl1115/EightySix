package com.utree.eightysix.app.feed;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.data.Promotion;
import com.utree.eightysix.utils.Utils;
import com.utree.eightysix.widget.AsyncImageView;

/**
 * @author simon
 */
public class FeedPromotionView extends FrameLayout {

  @InjectView (R.id.tv_content)
  public TextView mTvContent;

  @InjectView (R.id.tv_top_left)
  public TextView mTvTopLeft;

  @InjectView (R.id.tv_top_right)
  public TextView mTvTopRight;

  @InjectView (R.id.tv_bot_left)
  public TextView mTvBotLeft;

  @InjectView (R.id.tv_bot_right)
  public TextView mTvBotRight;

  @InjectView (R.id.aiv_bg)
  public AsyncImageView mAivBg;

  @InjectView (R.id.ll_bottom)
  public LinearLayout mLlBottom;

  private int mFactoryId;

  private Promotion mPromotion;

  public FeedPromotionView(Context context) {
    this(context, null, 0);
  }

  public FeedPromotionView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FeedPromotionView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    inflate(context, R.layout.item_feed_promotion, this);

    ButterKnife.inject(this, this);
  }

  public void setData(int factoryId, Promotion promotion) {
    mFactoryId = factoryId;
    mPromotion = promotion;

    if (mPromotion != null) {
      mTvTopLeft.setText(mPromotion.title);
      mTvTopRight.setText(mPromotion.activeName);
      mTvBotLeft.setText(mPromotion.activeRemark);
      mTvBotRight.setText(mPromotion.activeStartTime);
      mTvContent.setText(mPromotion.content);
      if (TextUtils.isEmpty(mPromotion.bgUrl)) {
        mTvContent.setBackgroundColor(Utils.strToColor(mPromotion.bgColor));
        mAivBg.setUrl(null);
      } else {
        mTvContent.setBackgroundColor(Color.TRANSPARENT);
        mAivBg.setUrl(mPromotion.bgUrl);
      }
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    if (mLlBottom.getVisibility() == VISIBLE) {
      widthSize += mLlBottom.getMeasuredHeight();
    }
    super.onMeasure(widthMeasureSpec, widthSize - U.dp2px(16) + MeasureSpec.EXACTLY);
  }

  @OnClick (R.id.tv_content)
  public void onTvContentClicked(View view) {
    BaseWebActivity.start(view.getContext(), mPromotion.activeWebViewName,
        String.format("%s%s?userid=%s&factoryid=%d", U.getConfig("api.host"),
            C.API_PROMOTION, Account.inst().getUserId(), mFactoryId));
  }
}
