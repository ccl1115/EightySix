package com.utree.eightysix.app.feed;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.StartPublishActivityEvent;
import com.utree.eightysix.data.QuestionSet;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
public class FeedQuestionView extends FrameLayout {

  @InjectView (R.id.tv_content)
  public TextView mContent;

  @InjectView (R.id.rb_ask_question)
  public RoundedButton mRbAskQuestion;

  @InjectView (R.id.iv_refresh)
  public ImageView mIvRefresh;

  @InjectView (R.id.aiv_bg)
  public AsyncImageView mAivBg;

  private QuestionSet mQuestionSet;

  private int mLastIndex = 0;

  public FeedQuestionView(Context context) {
    this(context, null);
  }

  public FeedQuestionView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    inflate(context, R.layout.item_feed_question, this);
    ButterKnife.inject(this, this);
  }

  public void setData(QuestionSet set) {
    mQuestionSet = set;

    if (mQuestionSet.lists.size() == 0) return;

    setQuestion();
  }

  @OnClick (R.id.iv_refresh)
  public void onIvRefreshClicked() {
    if (mQuestionSet != null && mQuestionSet.lists.size() != 0) {
      mLastIndex = mLastIndex < mQuestionSet.lists.size() - 1 ? mLastIndex + 1 : 0;
      setQuestion();
    }
  }

  @OnClick (R.id.rb_ask_question)
  public void onRbAskQuestionClicked() {
    U.getBus().post(new StartPublishActivityEvent());
  }

  protected void setQuestion() {
    QuestionSet.Question question = mQuestionSet.lists.get(mLastIndex);

    mContent.setText(question.content);
    if (TextUtils.isEmpty(question.bgUrl)) {
      mAivBg.setUrl(null);
      mContent.setBackgroundColor(ColorUtil.strToColor(question.bgColor));
    } else {
      mAivBg.setUrl(question.bgUrl);
      mContent.setBackgroundColor(Color.TRANSPARENT);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    super.onMeasure(widthMeasureSpec, widthSize - U.dp2px(16) + MeasureSpec.EXACTLY);
  }
}

