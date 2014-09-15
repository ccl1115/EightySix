package com.utree.eightysix.app.feed;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.OptionSet;
import com.utree.eightysix.request.SubmitAnswerRequest;
import com.utree.eightysix.response.OptionSetResponse;
import com.utree.eightysix.rest.*;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
public class FeedOptionSetView extends FrameLayout {

  private static final int STATE_SELECT = 0;
  private static final int STATE_INPUT = 1;
  private static final int STATE_CHOSEN = 2;
  private static final int STATE_PUBLISHED = 3;

  private OptionSet mData;

  private int mCurrent;

  private int mState;

  @InjectView(R.id.tv_title)
  public TextView mTvTitle;

  @InjectView(R.id.tv_sub_title)
  public TextView mTvSubTitle;

  @InjectView(R.id.tv_q1)
  public TextView mTvQ1;

  @InjectView(R.id.tv_q2)
  public TextView mTvQ2;

  @InjectView(R.id.tv_q3)
  public TextView mTvQ3;

  @InjectView(R.id.tv_other)
  public TextView mTvOther;

  @InjectView(R.id.et_other)
  public EditText mEtOther;

  @InjectView(R.id.rb_next)
  public RoundedButton mRbAction;

  @InjectView(R.id.tv_tip)
  public TextView mTvTip;

  @InjectView(R.id.fl_progress)
  public FrameLayout mFlProgress;

  private int mCircleId;

  @OnClick(R.id.iv_refresh)
  public void onIvRefreshClicked() {
    if (mData == null) {
      return;
    }

    if (mState == STATE_SELECT) {
      mCurrent = mCurrent == mData.options.size() - 1 ? 0 : mCurrent + 1;
    }

    switchToQuestion();
    setOption();
  }

  @OnClick(R.id.tv_q1)
  public void onTvQ1Clicked(View v) {
    requestSubmit(((TextView) v).getText().toString());
  }

  @OnClick(R.id.tv_q2)
  public void onTvQ2Clicked(View v) {
    requestSubmit(((TextView) v).getText().toString());
  }

  @OnClick(R.id.tv_q3)
  public void onTvQ3Clicked(View v) {
    requestSubmit(((TextView) v).getText().toString());
  }

  @OnClick(R.id.tv_other)
  public void onTvOtherClicked() {
    switchToOther();
  }

  @OnClick(R.id.rb_next)
  public void onRbNextClicked(View v) {
    if (mState == STATE_INPUT) {
      requestSubmit(mEtOther.getText().toString());
    } else if (mState == STATE_CHOSEN) {
      OptionPublishActivity.start(getContext(),
          mData.step2View.answerHelper,
          mData.step2View.viewName,
          mCircleId);
    }
  }

  public FeedOptionSetView(Context context) {
    this(context, null, 0);
  }

  public FeedOptionSetView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FeedOptionSetView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    LayoutInflater.from(context).inflate(R.layout.item_feed_options, this, true);
    setBackgroundResource(R.drawable.item_feed_options_bg);

    ButterKnife.inject(this);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
  }

  public void setData(int circleId, OptionSet data) {
    if (data == null) {
      return;
    }

    mData = data;
    mCircleId = circleId;

    if (mData.step == 0) {
      switchToQuestion();
      setOption();
    } else if (mData.step == 1) {
      if (mData.step2View != null) {
        switchToChosen();
        mRbAction.setText(mData.step2View.buttonText);
        mTvTitle.setText(mData.step2View.title);
        mTvSubTitle.setText(mData.step2View.subTitle);
        mTvTip.setText(mData.step2View.content);
      }
    }
  }

  private void setOption() {
    OptionSet.Option option = mData.options.get(mCurrent);
    mTvTitle.setText(option.title);
    mTvSubTitle.setText(option.subTitle);

    mTvQ1.setText(option.choices.get(0).text);
    mTvQ2.setText(option.choices.get(1).text);
    mTvQ3.setText(option.choices.get(2).text);
  }

  private void switchToOther() {
    mTvQ1.setVisibility(GONE);
    mTvQ2.setVisibility(GONE);
    mTvQ3.setVisibility(GONE);
    mTvOther.setVisibility(GONE);

    mEtOther.setVisibility(VISIBLE);

    mTvTip.setVisibility(GONE);

    mRbAction.setVisibility(VISIBLE);

    mState = STATE_INPUT;
  }

  private void switchToQuestion() {
    mTvQ1.setVisibility(VISIBLE);
    mTvQ2.setVisibility(VISIBLE);
    mTvQ3.setVisibility(VISIBLE);
    mTvOther.setVisibility(VISIBLE);
    mEtOther.setVisibility(GONE);

    mTvTip.setVisibility(GONE);

    mRbAction.setVisibility(GONE);

    mState = STATE_SELECT;
  }

  private void switchToChosen() {
    mTvQ1.setVisibility(GONE);
    mTvQ2.setVisibility(GONE);
    mTvQ3.setVisibility(GONE);
    mTvOther.setVisibility(GONE);
    mEtOther.setVisibility(GONE);

    mTvTip.setVisibility(VISIBLE);

    mRbAction.setVisibility(VISIBLE);

    mState = STATE_CHOSEN;
  }

  private void requestSubmit(String text) {
    showProgress();
    RequestData data = U.getRESTRequester().convert(
        new SubmitAnswerRequest(mCircleId,
            text,
            mData.options.get(mCurrent).quesId));

    U.getRESTRequester().request(data, new HandlerWrapper<OptionSetResponse>(data, new OnResponse2<OptionSetResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(OptionSetResponse response) {
        setData(mCircleId, response.object);
        hideProgress();
      }
    }, OptionSetResponse.class));
  }

  private void showProgress() {
    mFlProgress.setVisibility(VISIBLE);
    mFlProgress.setClickable(true);
  }

  private void hideProgress() {
    mFlProgress.setVisibility(GONE);
    mFlProgress.setClickable(false);
  }
}
