package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 */
public class TopBar extends ViewGroup implements ITopBar2 {

  public static final int TITLE_CLICK_MODE_ONE = 1;
  public static final int TITLE_CLICK_MODE_DIVIDE = 2;

  private int mTitleClickMode = TITLE_CLICK_MODE_ONE;

  private final Paint mTopLinePaint = new Paint();
  private final Paint mBotLinePaint = new Paint();

  @InjectView(R.id.tb_tv_bar_title)
  public TextView mTitle;

  @InjectView(R.id.tb_tv_sub_title)
  public TextView mSubTitle;

  @InjectView(R.id.tb_iv_action_left)
  public ImageView mIvActionLeft;

  @InjectView(R.id.tb_tv_action_left)
  public TextView mTvActionLeft;

  @InjectView(R.id.tb_iv_action_right)
  public ImageView mIvActionRight;

  @InjectView(R.id.tb_tv_action_right)
  public TextView mTvActionRight;

  @InjectView(R.id.tb_iv_search_close)
  public ImageView mIvSearchClose;

  @InjectView(R.id.tb_et_search)
  public EditText mEtSearch;

  @InjectView(R.id.tb_fl_left)
  public FrameLayout mFlLeft;

  @InjectView(R.id.tb_fl_right)
  public FrameLayout mFlRight;

  @InjectView(R.id.tb_rb_search)
  public RoundedButton mRbSearch;

  @InjectView(R.id.tb_ll_search)
  public LinearLayout mLlSearch;

  @InjectView(R.id.tb_ll_title)
  public LinearLayout mLlTitle;

  public ITopBar2.Callback mCallback2;

  private Callback mCallback;
  
  @InjectView(R.id.refresh_indicator)
  public RefreshIndicator mRefreshIndicator;


  @OnClick(R.id.tb_iv_action_left)
  public void onIvLeftClicked(View v) {
    if (mCallback2 != null) {
      mCallback2.onLeftClicked(v);
    }
  }

  @OnClick(R.id.tb_tv_action_left)
  public void onTvLeftClicked(View v) {
    if (mCallback2 != null) {
      mCallback2.onLeftClicked(v);
    }
  }
  
  @OnClick(R.id.tb_iv_action_right)
  public void onIvRightClicked(View v) {
    if (mCallback2 != null) {
      mCallback2.onRightClicked(v);
    }
  }

  @OnClick(R.id.tb_tv_action_right)
  public void onTvRightClicked(View v) {
    if (mCallback2 != null) {
      mCallback2.onRightClicked(v);
    }
  }

  public TopBar(Context context) {
    this(context, null, R.attr.topBarStyle);
  }

  public TopBar(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.topBarStyle);
  }

  public TopBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    View.inflate(context, R.layout.widget_top_bar, this);
    U.viewBinding(this, this);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar, defStyle, 0);

    mTitle.setTextColor(ta.getColor(R.styleable.TopBar_titleColor, Color.GRAY));

    mTopLinePaint.setColor(ta.getColor(R.styleable.TopBar_topLineColor, Color.GRAY));
    mBotLinePaint.setColor(ta.getColor(R.styleable.TopBar_botLineColor, Color.GRAY));

    mTopLinePaint.setStrokeWidth(1);
    mBotLinePaint.setStrokeWidth(1);

    mRbSearch.setEnabled(false);

    setTitleClickMode(TITLE_CLICK_MODE_ONE);
  }

  public String getTitle() {
    return mTitle.getText().toString();
  }

  @Override
  public void setTitle(String title) {
    mTitle.setText(title);
  }

  @Override
  public void setLeftText(String text) {
    setLeftStyle(ITopBar2.STYLE_TEXT);
    mTvActionLeft.setText(text);
  }

  @Override
  public void setLeftStyle(int style) {
    if (style == ITopBar2.STYLE_IMAGE) {
      mIvActionLeft.setVisibility(VISIBLE);
      mTvActionLeft.setVisibility(GONE);
    } else if (style == ITopBar2.STYLE_TEXT) {
      mIvActionLeft.setVisibility(GONE);
      mTvActionLeft.setVisibility(VISIBLE);
    }
  }

  public EditText getSearchEditText() {
    return mEtSearch;
  }

  public String getSubTitle() {
    return mSubTitle.getText().toString();
  }

  @Override
  public void setSubTitle(String subTitle) {
    mSubTitle.setVisibility(TextUtils.isEmpty(subTitle) ? GONE : VISIBLE);
    mSubTitle.setText(subTitle);
  }

  @Override
  public void setRightText(String text) {
    setRightStyle(ITopBar2.STYLE_TEXT);
    mTvActionRight.setText(text);
  }

  @Override
  public void setRightStyle(int style) {
    if (style == ITopBar2.STYLE_IMAGE) {
      mIvActionRight.setVisibility(VISIBLE);
      mTvActionRight.setVisibility(GONE);
    } else if (style == ITopBar2.STYLE_TEXT) {
      mTvActionRight.setVisibility(VISIBLE);
      mIvActionRight.setVisibility(GONE);
    }
  }

  @Override
  public void setCallback(ITopBar2.Callback callback) {
    mCallback2 = callback;
  }

  @Deprecated
  public void setActionAdapter(ActionAdapter actionAdapter) {
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public void enterSearch() {
    mLlSearch.setVisibility(VISIBLE);
    if (mCallback != null) mCallback.onEnterSearch();
  }

  public void exitSearch() {
    mLlSearch.setVisibility(INVISIBLE);
    if (mCallback != null) mCallback.onExitSearch();
  }

  @OnClick(R.id.tb_iv_search_close)
  public void onIvSearchCloseClicked() {
    mEtSearch.setText("");
  }

  @OnClick(R.id.tb_fl_left)
  public void onActionLeftClicked() {
    if (mTitleClickMode == TITLE_CLICK_MODE_ONE) {
      if (mCallback != null) mCallback.onActionLeftClicked();
    }
  }

  @OnClick(R.id.tb_ll_title)
  public void onLlTitleClicked() {
    if (mTitleClickMode == TITLE_CLICK_MODE_DIVIDE) {
      if (mCallback != null) mCallback.onTitleClicked();
    }
  }

  @OnClick(R.id.tb_rb_search)
  public void onRbSearchClicked() {
    if (mCallback != null) mCallback.onActionSearchClicked(mEtSearch.getText());
  }

  @OnTextChanged(R.id.tb_et_search)
  public void onEtSearchTextChanged(CharSequence cs) {
    if (cs.length() == 0) {
      mIvSearchClose.setVisibility(INVISIBLE);
      mRbSearch.setEnabled(false);
    } else {
      mRbSearch.setEnabled(true);
      mIvSearchClose.setVisibility(VISIBLE);
    }
    if (mCallback != null) mCallback.onSearchTextChanged(cs);
  }

  public ActionButton getActionView(int position) {
    return null;
  }

  public ActionButton getActionOverflow() {
    return null;
  }

  public void setTitleClickMode(int mode) {
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    // draw top line
    canvas.drawLine(0, 0, getMeasuredWidth(), 0, mTopLinePaint);
    canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, mBotLinePaint);

    super.dispatchDraw(canvas);
  }

  @Override
  protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof LayoutParams;
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    final int height = b - t;

    mFlLeft.layout(0, 0, mFlLeft.getMeasuredWidth(), b);

    mLlTitle.layout((getMeasuredWidth() - mLlTitle.getMeasuredWidth()) >> 1,
        (getMeasuredHeight() - mLlTitle.getMeasuredHeight()) >> 1,
        (getMeasuredWidth() + mLlTitle.getMeasuredWidth()) >> 1,
        (getMeasuredHeight() + mLlTitle.getMeasuredHeight()) >> 1);
    
    mRefreshIndicator.layout(l, t, r, b);

    mLlSearch.layout(0, 0, mLlSearch.getMeasuredWidth(), b);
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  @Override
  public boolean shouldDelayChildPressedState() {
    return false;
  }

  @SuppressWarnings("SuspiciousNameCombination")
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    final int widthSize = widthMeasureSpec & ~(0x3 << 30);

    measureChild(mFlLeft, widthSize + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    measureChild(mLlSearch, widthSize - getLeft() + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

    measureChild(mLlTitle, widthSize + MeasureSpec.AT_MOST, heightSize + MeasureSpec.AT_MOST);

    measureChild(mRefreshIndicator, widthSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);
    
    setMeasuredDimension(widthSize, heightSize);
  }

  public interface ActionAdapter {
    String getTitle(int position);

    Drawable getIcon(int position);

    Drawable getBackgroundDrawable(int position);

    void onClick(View view, int position);

    int getCount();

    LayoutParams getLayoutParams(int position);
  }

  public interface Callback {
    void onActionLeftClicked();

    void onActionOverflowClicked();

    boolean showActionOverflow();

    void onEnterSearch();

    void onExitSearch();

    void onSearchTextChanged(CharSequence cs);

    void onActionSearchClicked(CharSequence cs);

    void onTitleClicked();

    void onIconClicked();
  }

  public static class LayoutParams extends MarginLayoutParams {

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }
  }
}
