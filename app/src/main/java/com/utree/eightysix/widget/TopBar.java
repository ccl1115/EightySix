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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import de.akquinet.android.androlog.Log;

/**
 */
public class TopBar extends ViewGroup implements View.OnClickListener {

  public static final int TITLE_CLICK_MODE_ONE = 1;
  private int mTitleClickMode = TITLE_CLICK_MODE_ONE;
  public static final int TITLE_CLICK_MODE_DIVIDE = 2;

  private final Paint mTopLinePaint = new Paint();
  private final Paint mBotLinePaint = new Paint();

  @InjectView(R.id.tb_tv_bar_title)
  public TextView mTitle;

  @InjectView(R.id.tb_tv_sub_title)
  public TextView mSubTitle;

  @InjectView(R.id.tb_ab_right)
  public ActionButton mAbRight;

  @InjectView(R.id.tb_ab_left)
  public ActionButton mAbLeft;

  @InjectView(R.id.tb_iv_search_close)
  public ImageView mIvSearchClose;

  @InjectView(R.id.tb_et_search)
  public EditText mEtSearch;

  @InjectView(R.id.tb_rb_search)
  public RoundedButton mRbSearch;

  @InjectView(R.id.tb_ll_search)
  public LinearLayout mLlSearch;

  @InjectView(R.id.tb_ll_title)
  public LinearLayout mLlTitle;

  @InjectView(R.id.tb_iv_indicator)
  public ImageView mIvIndicator;

  @InjectView(R.id.refresh_indicator)
  public RefreshIndicator mRefreshIndicator;

  @InjectView(R.id.tb_ll_title_tab)
  public LinearLayout mLlTitleTab;

  private Callback mCallback;

  private TitleAdapter mTitleAdapter;

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


    setOnClickListener(this);

    mRbSearch.setEnabled(false);

    setTitleClickMode(TITLE_CLICK_MODE_ONE);
  }

  public String getTitle() {
    return mTitle.getText().toString();
  }

  public void setTitle(String title) {
    mTitle.setText(title);
    mTitle.setVisibility(VISIBLE);
    mLlTitleTab.setVisibility(GONE);
  }

  public EditText getSearchEditText() {
    return mEtSearch;
  }

  public String getSubTitle() {
    return mSubTitle.getText().toString();
  }

  public void setSubTitle(String subTitle) {
    mSubTitle.setVisibility(TextUtils.isEmpty(subTitle) ? GONE : VISIBLE);
    mSubTitle.setText(subTitle);
  }

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

  @Override
  public void onClick(View v) {
  }

  @OnClick(R.id.tb_iv_search_close)
  public void onIvSearchCloseClicked() {
    mEtSearch.setText("");
  }

  @OnClick(R.id.tb_ab_left)
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

  @OnClick(R.id.tb_ab_right)
  public void onActionOverflowClicked(View v) {
    if (mCallback != null) mCallback.onActionOverflowClicked();
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
    return getAbRight();
  }

  public void setTitleAdapter(TitleAdapter adapter) {
    mTitleAdapter = adapter;
    mTitle.setVisibility(GONE);
    mLlTitleTab.setVisibility(VISIBLE);

    final int count = mTitleAdapter.getCount();

    mLlTitleTab.removeAllViews();

    final int p = U.dp2px(4);
    for (int i = 0; i < count; i++) {
      TextView t = new TextView(getContext());
      t.setTextColor(getResources().getColorStateList(R.color.apptheme_primary_text_light));
      t.setPadding(4 * p, p, 4 * p, p);
      t.setText(mTitleAdapter.getTitle(i));

      if (i == 0) {
        t.setBackgroundResource(R.drawable.tb_title_left);
      } else if (i == count - 1) {
        t.setBackgroundResource(R.drawable.tb_title_right);
      } else {
        t.setBackgroundResource(R.drawable.tb_title_center);
      }

      final int finalI = i;
      t.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            mTitleAdapter.onSelected(v, finalI);
          }

          for (int i = 0, size = mLlTitleTab.getChildCount(); i < size; i++) {
            mLlTitleTab.getChildAt(i).setSelected(false);
          }

          v.setSelected(!v.isSelected());
        }
      });
      mLlTitleTab.addView(t);
    }
  }

  public void setTitleTabSelected(int position) {
    for (int i = 0, size = mLlTitleTab.getChildCount(); i < size; i++) {
      mLlTitleTab.getChildAt(i).setSelected(false);
    }

    View childAt = mLlTitleTab.getChildAt(position);
    childAt.setSelected(true);
  }

  public void setTitleTabText(int position, String text) {
    View view = mLlTitleTab.getChildAt(position);
    ((TextView) view).setText(text);
  }

  public void setTitleClickMode(int mode) {
    mTitleClickMode = mode;

    if (mTitleClickMode == TITLE_CLICK_MODE_ONE) {
      mLlTitle.setClickable(false);
      mIvIndicator.setVisibility(GONE);
    } else {
      mLlTitle.setClickable(true);
      mIvIndicator.setVisibility(VISIBLE);
    }
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
    final int width = r - l;

    mAbLeft.layout(0, 0, mAbLeft.getMeasuredWidth(), b);

    mAbRight.layout(r - mAbRight.getMeasuredWidth(), 0, r, b);

    mLlTitle.layout((width - mLlTitle.getMeasuredWidth()) >> 1,
        (height - mLlTitle.getMeasuredHeight()) >> 1,
        (width + mLlTitle.getMeasuredWidth()) >> 1,
        (height + mLlTitle.getMeasuredHeight()) >> 1);

    mLlSearch.layout(mAbLeft.getRight(), 0, mAbLeft.getRight() + mLlSearch.getMeasuredWidth(), b);

    mRefreshIndicator.layout(0, 0, r - l, b - t);
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    Log.d("TopBar", "generateLayoutParams from attrs");
    return new LayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    Log.d("TopBar", "generateLayoutParams from source");
    return new LayoutParams(p);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    Log.d("TopBar", "generateDefaultLayoutParams");
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

    int widthLeft = widthSize;

    measureChild(mAbRight, widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    widthLeft -= mAbRight.getMeasuredWidth();

    measureChild(mAbLeft, widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    widthLeft -= mAbLeft.getMeasuredWidth();

    measureChild(mLlTitle, widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    measureChild(mLlSearch, widthSize - mAbLeft.getRight() + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

    measureChild(mRefreshIndicator, widthSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

    setMeasuredDimension(widthSize, heightSize);
  }

  public ActionButton getAbRight() {
    return mAbRight;
  }

  public ActionButton getAbLeft() {
    return mAbLeft;
  }

  public interface ActionAdapter {
    String getTitle(int position);

    Drawable getIcon(int position);

    Drawable getBackgroundDrawable(int position);

    void onClick(View view, int position);

    int getCount();

    LayoutParams getLayoutParams(int position);
  }

  public interface TitleAdapter {
    String getTitle(int position);

    void onSelected(View view, int position);

    int getCount();
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
