/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.data.FeedSign;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
public class FeedSignView extends LinearLayout {

  @InjectView(R.id.tv_text)
  public TextView mTvText;

  @InjectView(R.id.rb_sign)
  public RoundedButton mRbSign;

  private FeedSign mFeedSign;

  public FeedSignView(Context context) {
    this(context, null, 0);
  }

  public FeedSignView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FeedSignView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    inflate(context, R.layout.item_feed_sign, this);

    ButterKnife.inject(this, this);
  }

  public void setData(FeedSign feedSign) {
    mFeedSign = feedSign;

    if (mFeedSign.signed == 0) {
      mTvText.setText(String.format("你已连续打卡%d天，近一个月漏打卡%d天",
          mFeedSign.signConsecutiveTimes, mFeedSign.signMissingTimes));
      mRbSign.setText("打卡");
    } else {
      mTvText.setText(String.format("你已连续打卡%d天，今天你是全蓝莓第%d个打卡的",
          mFeedSign.signConsecutiveTimes, mFeedSign.signMissingTimes));
      mRbSign.setText("已打卡");
    }

  }

}
