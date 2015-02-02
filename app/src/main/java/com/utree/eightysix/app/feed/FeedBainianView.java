/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.data.Bainian;

/**
 */
public final class FeedBainianView extends FrameLayout {

  @InjectView(R.id.et_recipient)
  public EditText mEtRecipient;

  @InjectView(R.id.et_msg)
  public EditText mEtMsg;

  @InjectView(R.id.tv_title)
  public TextView mTvTitle;

  @InjectView(R.id.tv_sub_title)
  public TextView mTvSubTitle;

  @InjectView(R.id.tv_tip)
  public TextView mTvTip;

  @InjectView(R.id.rb_generate)
  public TextView mRbGenerate;

  private int mIndex;
  private Bainian mBainian;

  public FeedBainianView(Context context) {
    this(context, null, 0);
  }

  public FeedBainianView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FeedBainianView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    LayoutInflater.from(context).inflate(R.layout.item_feed_bainian, this, true);

    ButterKnife.inject(this);
  }

  public void setData(Bainian bainian) {
    mBainian = bainian;
    mEtRecipient.setHint(mBainian.receiveText);
    mTvTitle.setText(mBainian.title);
    mTvSubTitle.setText(mBainian.subTitle);
    mTvTip.setText(mBainian.receiveText);
    mRbGenerate.setText(mBainian.buttonText);
    mEtMsg.setText(mBainian.newYearContents.get(mIndex).content);
  }

  @OnClick(R.id.iv_refresh)
  public void onIvRefreshClicked() {
    mIndex = mIndex >= mBainian.newYearContents.size() - 1 ? 0 : mIndex + 1;
    mEtMsg.setText(mBainian.newYearContents.get(mIndex).content);
  }
}
