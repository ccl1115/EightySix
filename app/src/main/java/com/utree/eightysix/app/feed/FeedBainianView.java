/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
public class FeedBainianView extends FrameLayout {

  @InjectView(R.id.et_recipient)
  public EditText mEtRecipient;

  @InjectView(R.id.et_msg)
  public EditText mEtMsg;

  @InjectView(R.id.rb_generate)
  public RoundedButton mRbGenerate;

  @InjectView(R.id.iv_refresh)
  public ImageView mIvRefresh;

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

}
