package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
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

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TopBar extends ViewGroup implements View.OnClickListener {

  public static final int TITLE_CLICK_MODE_ONE = 1;
  private int mTitleClickMode = TITLE_CLICK_MODE_ONE;
  public static final int TITLE_CLICK_MODE_DIVIDE = 2;
  private final List<ActionButton> mActionViews = new ArrayList<ActionButton>();
  private final Paint mTopLinePaint = new Paint();
  private final Paint mBotLinePaint = new Paint();

  @InjectView (R.id.tb_tv_bar_title)
  public TextView mTitle;

  @InjectView (R.id.tb_tv_sub_title)
  public TextView mSubTitle;

  @InjectView (R.id.tb_iab_action_overflow)
  public ImageActionButton mActionOverFlow;

  @InjectView (R.id.tb_iv_action_left)
  public ImageView mActionLeft;

  @InjectView (R.id.tb_iv_search_close)
  public ImageView mIvSearchClose;

  @InjectView (R.id.tb_et_search)
  public EditText mEtSearch;

  @InjectView (R.id.tb_ll_left)
  public LinearLayout mLlLeft;

  @InjectView (R.id.tb_iv_app_icon)
  public ImageView mIvAppIcon;

  @InjectView (R.id.tb_rb_search)
  public RoundedButton mRbSearch;

  @InjectView (R.id.tb_ll_search)
  public LinearLayout mLlSearch;

  @InjectView (R.id.tb_ll_title)
  public LinearLayout mLlTitle;

  @InjectView (R.id.tb_ll_icon)
  public LinearLayout mLlIcon;

  @InjectView (R.id.tb_iv_indicator)
  public ImageView mIvIndicator;

  @InjectView (R.id.tb_v_divider)
  public View mVDivider;

  @InjectView(R.id.refresh_indicator)
  public RefreshIndicator mRefreshIndicator;

  private Callback mCallback;
  private ActionAdapter mActionAdapter;
  private int mCurCount;

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

    mActionOverFlow.setBackgroundDrawable(getResources().getDrawable(R.drawable.apptheme_primary_btn_dark));

    setOnClickListener(this);

    mRbSearch.setEnabled(false);

    setTitleClickMode(TITLE_CLICK_MODE_ONE);
  }

  public String getTitle() {
    return mTitle.getText().toString();
  }

  public void setTitle(String title) {
    mTitle.setText(title);
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
    mActionAdapter = actionAdapter;
    mCurCount = mActionAdapter == null ? 0 : mActionAdapter.getCount();
    if (mCurCount < 0) throw new IllegalArgumentException("Count less than 0");

    for (View v : mActionViews) {
      v.setOnClickListener(null);
      v.setBackgroundDrawable(null);
      removeView(v);
    }
    mActionViews.clear();

    for (int i = 0; i < mCurCount; i++) {
      ActionButton view;
      LayoutParams layoutParams = mActionAdapter.getLayoutParams(i);
      if (layoutParams == null)
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      if (TextUtils.isEmpty(mActionAdapter.getTitle(i))) {
        view = buildActionItemView(mActionAdapter.getIcon(i), mActionAdapter.getBackgroundDrawable(i), layoutParams);
      } else {
        view = buildActionItemView(mActionAdapter.getTitle(i), mActionAdapter.getBackgroundDrawable(i), layoutParams);
      }
      addView(view, 0);
      mActionViews.add(view);
    }
    requestLayout();
    invalidate();
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

    final int id = v.getId();

    switch (id) {
      default:
        for (int i = 0; i < mActionViews.size(); i++) {
          View view = mActionViews.get(i);

          if (view.equals(v)) {
            mActionAdapter.onClick(view, i);
            break;
          }
        }
        break;
    }
  }

  @OnClick (R.id.tb_iv_search_close)
  public void onIvSearchCloseClicked() {
    mEtSearch.setText("");
  }

  @OnClick (R.id.tb_ll_left)
  public void onActionLeftClicked() {
    if (mTitleClickMode == TITLE_CLICK_MODE_ONE) {
      if (mCallback != null) mCallback.onActionLeftClicked();
    }
  }

  @OnClick (R.id.tb_ll_title)
  public void onLlTitleClicked() {
    if (mTitleClickMode == TITLE_CLICK_MODE_DIVIDE) {
      if (mCallback != null) mCallback.onTitleClicked();
    }
  }

  @OnClick (R.id.tb_ll_icon)
  public void onLlIconClicked() {
    if (mTitleClickMode == TITLE_CLICK_MODE_DIVIDE) {
      if (mCallback != null) mCallback.onIconClicked();
    }
  }


  @OnClick (R.id.tb_iab_action_overflow)
  public void onActionOverflowClicked(View v) {
    if (mCallback != null) mCallback.onActionOverflowClicked();
  }

  @OnClick (R.id.tb_rb_search)
  public void onRbSearchClicked() {
    if (mCallback != null) mCallback.onActionSearchClicked(mEtSearch.getText());
  }

  @OnTextChanged (R.id.tb_et_search)
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
    return mActionViews.get(position);
  }

  public ActionButton getActionOverflow() {
    return mActionOverFlow;
  }

  public void setTitleClickMode(int mode) {
    mTitleClickMode = mode;

    if (mTitleClickMode == TITLE_CLICK_MODE_ONE) {
      mLlTitle.setClickable(false);
      mLlIcon.setClickable(false);
      mLlLeft.setClickable(true);
      mIvIndicator.setVisibility(GONE);
      mVDivider.setVisibility(GONE);
    } else {
      mLlTitle.setClickable(true);
      mLlIcon.setClickable(true);
      mLlLeft.setClickable(false);
      mIvIndicator.setVisibility(VISIBLE);
      mVDivider.setVisibility(VISIBLE);
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

    mLlLeft.layout(0, 0, mLlLeft.getMeasuredWidth(), b);

    mLlSearch.layout(mIvAppIcon.getRight(), 0, mIvAppIcon.getRight() + mLlSearch.getMeasuredWidth(), b);

    mRefreshIndicator.layout(l, t, r, b);

    mActionOverFlow.layout(r - mActionOverFlow.getMeasuredWidth(), 0, r, b);

    r -= mActionOverFlow.getMeasuredWidth();

    if (mCurCount != 0) {
      for (View child : mActionViews) {
        MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
        r -= params.rightMargin;
        r -= child.getMeasuredWidth();
        child.layout(r,
            (height - child.getMeasuredHeight()) >> 1,
            r + child.getMeasuredWidth(),
            (height + child.getMeasuredHeight()) >> 1);
        r -= params.leftMargin;
      }
    }
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

  @SuppressWarnings ("SuspiciousNameCombination")
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    final int widthSize = widthMeasureSpec & ~(0x3 << 30);

    int widthLeft = widthSize;

    if (mCallback != null && mCallback.showActionOverflow()) {
      measureChild(mActionOverFlow, (int) (heightSize * 0.9f) + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);
    }

    widthLeft -= mActionOverFlow.getMeasuredWidth();

    if (mActionAdapter != null) {
      if (mCurCount != mActionAdapter.getCount()) {
        throw new IllegalStateException("Adapter count updates");
      }
      if (mCurCount != 0) {
        for (View view : mActionViews) {
          if (widthLeft < heightSize) {
            measureChild(view, MeasureSpec.EXACTLY, heightSize + MeasureSpec.AT_MOST);
          } else {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            int childHeightSpec, childWidthSpec;
            switch (lp.height) {
              case LayoutParams.WRAP_CONTENT:
                childHeightSpec = heightSize + MeasureSpec.AT_MOST;
                break;
              case LayoutParams.MATCH_PARENT:
                childHeightSpec = heightSize + MeasureSpec.EXACTLY;
                break;
              default:
                childHeightSpec = lp.height + MeasureSpec.EXACTLY;
                break;
            }

            switch (lp.width) {
              case LayoutParams.WRAP_CONTENT:
                childWidthSpec = widthLeft + MeasureSpec.AT_MOST;
                break;
              case LayoutParams.MATCH_PARENT:
                childWidthSpec = (int) (heightSize * 0.9f) + MeasureSpec.EXACTLY;
                break;
              default:
                childWidthSpec = lp.width + MeasureSpec.EXACTLY;
                break;
            }
            measureChild(view, childWidthSpec, childHeightSpec);
          }
          widthLeft -= view.getMeasuredWidth();
        }
      }
    }

    measureChild(mLlLeft, widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    measureChild(mLlSearch, widthSize - mIvAppIcon.getRight() + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

    measureChild(mRefreshIndicator, widthSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);


    setMeasuredDimension(widthSize, heightSize);
  }

  private ActionButton buildActionItemView(Drawable drawable, Drawable backgroundDrawable, LayoutParams layoutParams) {
    if (drawable == null) return null;

    final ImageActionButton imageView = new ImageActionButton(getContext());
    imageView.setImageDrawable(drawable);
    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    imageView.setLayoutParams(layoutParams);
    imageView.setActionBackgroundDrawable(backgroundDrawable);
    imageView.setOnClickListener(this);

    return imageView;
  }

  private ActionButton buildActionItemView(String text, Drawable backgroundDrawable, LayoutParams layoutParams) {
    if (TextUtils.isEmpty(text)) return null;

    final TextActionButton button = new TextActionButton(getContext());
    button.setActionBackgroundDrawable(backgroundDrawable);
    button.setLayoutParams(layoutParams);
    button.setTextSize(14);
    button.setText(text);
    button.setGravity(Gravity.CENTER);
    button.setTextColor(getResources().getColor(R.color.apptheme_primary_text_light));
    button.setSingleLine(true);
    button.setLines(1);
    button.setOnClickListener(this);
    final int hPadding = U.dp2px(8);
    final int vPadding = U.dp2px(6);
    button.setActionPadding(hPadding, vPadding, hPadding, vPadding);
    return button;
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
