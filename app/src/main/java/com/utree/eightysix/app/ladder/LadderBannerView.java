/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.ladder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.response.BaseLadderResponse;
import com.utree.eightysix.utils.CmdHandler;
import com.utree.eightysix.widget.AsyncImageView;

/**
 */
public class LadderBannerView extends LinearLayout {

  @InjectView(R.id.aiv_banner)
  public AsyncImageView mAivBanner;

  @InjectView(R.id.tv_info)
  public TextView mTvInfo;

  private BaseLadderResponse.Extra mExtra;

  @OnClick(R.id.aiv_banner)
  public void onAivBannerClicked(View v) {
    CmdHandler.inst().handle(v.getContext(), mExtra.banner.cmd);
  }

  public LadderBannerView(Context context) {
    this(context, null, 0);
  }

  public LadderBannerView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LadderBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.head_ladder_banner, this);

    ButterKnife.inject(this, this);
  }


  public void setData(BaseLadderResponse.Extra extra) {
    mExtra = extra;
    mTvInfo.setText(extra.info);
    mAivBanner.setUrl(extra.banner.bgUrl);
  }
}
