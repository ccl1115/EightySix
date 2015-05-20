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
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.FeedSign;
import com.utree.eightysix.response.FeedSignResultResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
public class FeedSignView extends LinearLayout {

  @InjectView(R.id.tv_text)
  public TextView mTvText;

  @InjectView(R.id.rb_sign)
  public RoundedButton mRbSign;

  private FeedSign mFeedSign;
  private int mFactoryId;

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

  @OnClick(R.id.rb_sign)
  public void onRbSignClick() {
    if (mFeedSign != null && mFeedSign.signed == 0) {
      sign();
    }
  }

  public void setData(FeedSign feedSign, int factoryId) {
    mFeedSign = feedSign;
    mFactoryId = factoryId;

    if (mFeedSign.signed == 0) {
      mRbSign.setEnabled(true);
      mTvText.setText(String.format("你已连续打卡%d天，近一个月漏打卡%d天",
          mFeedSign.signConsecutiveTimes, mFeedSign.signMissingTimes));
      mRbSign.setText("打卡");
    } else {
      mRbSign.setEnabled(false);
      mTvText.setText(String.format("你已连续打卡%d天，今天你是全蓝莓第%d个打卡的",
          mFeedSign.signConsecutiveTimes, mFeedSign.rank));
      mRbSign.setText("已打卡");
    }
  }

  private void sign() {
    U.request("userfactory_sign_create", new OnResponse2<FeedSignResultResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FeedSignResultResponse response) {
        if (RESTRequester.responseOk(response)) {
          mFeedSign.signed = 1;
          mTvText.setText(String.format("你已连续打卡%d天，今天你是全蓝莓第%d个打卡的",
              response.object.consecutiveTimes, response.object.rank));
          mRbSign.setText("已打卡");
          mRbSign.setEnabled(false);
          U.showToast(String.format("打卡成功，获得%d枚蓝星，%d经验值",
              response.object.bluestar, response.object.experience));
        }
      }
    }, FeedSignResultResponse.class, mFactoryId);
  }
}
