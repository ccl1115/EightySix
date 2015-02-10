/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.squareup.picasso.Picasso;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.web.BainianWebActivity;
import com.utree.eightysix.data.Bainian;
import com.utree.eightysix.widget.ThemedDialog;

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

  @InjectView(R.id.rb_generate)
  public TextView mRbGenerate;

  @InjectView(R.id.iv_bg)
  public ImageView mIvBg;

  private boolean mTextChanged;

  private int mIndex;
  private Bainian mBainian;

  @OnTextChanged(R.id.et_msg)
  public void onEtMsgTextChanged(CharSequence cs) {
    mTextChanged = true;
  }

  @OnClick(R.id.rb_generate)
  public void onRbGenerateClicked() {
    if (TextUtils.isEmpty(mEtMsg.getText()) ){
      U.showToast("祝福语还未输入哦");
      return;
    }
    if (TextUtils.isEmpty(mEtRecipient.getText())) {
      U.showToast("被祝福者的名字还未输入哦");
      return;
    }
    BainianWebActivity.start(getContext(), mEtRecipient.getText().toString(), mEtMsg.getText().toString());
  }

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
    Picasso.with(getContext()).load(R.drawable.bg_feed_bainian).into(mIvBg);
    mBainian = bainian;
    mEtRecipient.setHint(mBainian.receiveText);
    mTvTitle.setText(mBainian.title);
    mTvSubTitle.setText(mBainian.subTitle);
    mRbGenerate.setText(mBainian.buttonText);
    mEtMsg.setText(mBainian.newYearContents.get(mIndex).content);
  }

  @OnClick(R.id.iv_refresh)
  public void onIvRefreshClicked() {
    if (mTextChanged) {
      showChangeTextDialog();
    } else {
      changeText();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec - U.dp2px(8));
  }

  private void showChangeTextDialog() {
    final ThemedDialog dialog = new ThemedDialog(getContext());

    dialog.setTitle("提醒");

    TextView view = new TextView(getContext());

    view.setText("换一换后，会删除掉之前编辑的祝福语哦！");
    int p = U.dp2px(16);
    view.setPadding(p, p, p, p);

    dialog.setContent(view);

    dialog.setPositive("换一换", new OnClickListener() {
      @Override
      public void onClick(View v) {
        changeText();
        dialog.dismiss();
      }
    });

    dialog.setRbNegative("取消", new OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  public void changeText() {
    mIndex = mIndex >= mBainian.newYearContents.size() - 1 ? 0 : mIndex + 1;
    mEtMsg.setText(mBainian.newYearContents.get(mIndex).content);
    mTextChanged = false;
  }
}
