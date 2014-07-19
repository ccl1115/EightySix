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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TopBar extends ViewGroup implements View.OnClickListener {

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

  @InjectView (R.id.tb_rl_left)
  public RelativeLayout mRlTitle;

  @InjectView (R.id.tb_iv_app_icon)
  public ImageView mIvAppIcon;

  @InjectView (R.id.tb_rb_search)
  public RoundedButton mRbSearch;

  @InjectView (R.id.tb_ll_search)
  public LinearLayout mLlSearch;

  private Callback mCallback;
  private ActionAdapter mActionAdapter;
  private int mCurCount;

  public TopBar(Context context) {
    this(context, null);
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
        layoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      if (TextUtils.isEmpty(mActionAdapter.getTitle(i))) {
        view = buildActionItemView(mActionAdapter.getIcon(i), mActionAdapter.getBackgroundDrawable(i), layoutParams);
      } else {
        view = buildActionItemView(mActionAdapter.getTitle(i), mActionAdapter.getBackgroundDrawable(i), layoutParams);
      }
      addView(view);
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

  @OnClick (R.id.tb_rl_left)
  public void onActionLeftClicked(View v) {
    if (mCallback != null) mCallback.onActionLeftClicked();
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
    } else {
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
            view.measure(MeasureSpec.EXACTLY, heightSize + MeasureSpec.AT_MOST);
          } else {
            LayoutParams lp = view.getLayoutParams();
            int childHeightSpec = 0, childWidthSpec = 0;
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

    measureChild(mRlTitle, widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    measureChild(mLlSearch, widthSize - mIvAppIcon.getRight() + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);


    setMeasuredDimension(widthSize, heightSize);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    // draw top line
    canvas.drawLine(0, 0, getMeasuredWidth(), 0, mTopLinePaint);
    canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, mBotLinePaint);

    super.dispatchDraw(canvas);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    final int height = b - t;

    mRlTitle.layout(0, 0, mRlTitle.getMeasuredWidth(), b);

    mLlSearch.layout(mIvAppIcon.getRight(), 0, mIvAppIcon.getRight() + mLlSearch.getMeasuredWidth(), b);

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
    button.setLayoutParams(layoutParams);
    button.setActionBackgroundDrawable(backgroundDrawable);
    button.setTextSize(14);
    button.setText(text);
    button.setGravity(Gravity.CENTER);
    button.setTextColor(getResources().getColor(R.color.apptheme_primary_text_light));
    button.setSingleLine(true);
    button.setLines(1);
    button.setOnClickListener(this);
    final int hPadding = U.dp2px(6);
    final int vPadding = U.dp2px(4);
    button.setActionPadding(hPadding, vPadding, hPadding, vPadding);
    return button;
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new MarginLayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateLayoutParams(LayoutParams p) {
    return new MarginLayoutParams(p);
  }

  public interface ActionAdapter {
    String getTitle(int position);

    Drawable getIcon(int position);

    Drawable getBackgroundDrawable(int position);

    void onClick(View view, int position);

    int getCount();

    FrameLayout.LayoutParams getLayoutParams(int position);
  }

  public interface Callback {
    void onActionLeftClicked();

    void onActionOverflowClicked();

    boolean showActionOverflow();

    void onEnterSearch();

    void onExitSearch();

    void onSearchTextChanged(CharSequence cs);

    void onActionSearchClicked(CharSequence cs);
  }
}
