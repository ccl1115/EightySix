package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TopBar extends ViewGroup implements View.OnClickListener {

  public static final int DEFAULT_RIGHT_PADDING = 5;
  private static final int MINIMIUM_TITLE_WIDTH = 60;
  private final List<View> mActionViews = new ArrayList<View>();

  @InjectView (R.id.tb_tv_bar_title)
  public TextView mTitle;

  @InjectView (R.id.tb_tv_sub_title)
  public TextView mSubTitle;

  @InjectView (R.id.tb_iv_action_overflow)
  public ImageView mActionOverFlow;

  @InjectView (R.id.tb_iv_action_left)
  public ImageView mActionLeft;

  @InjectView (R.id.tb_fl_search)
  public FrameLayout mFlSearch;

  @InjectView (R.id.tb_iv_search_close)
  public ImageView mIvSearchClose;

  @InjectView (R.id.tb_et_search)
  public EditText mEtSearch;

  @InjectView (R.id.tb_rl_left)
  public RelativeLayout mRlTitle;

  @InjectView (R.id.tb_iv_app_icon)
  public ImageView mIvAppIcon;

  private OnClickListener mOnActionOverflowClickListener;
  private OnClickListener mOnActionLeftClickListener;

  private ActionAdapter mActionAdapter;
  private ActionAdapter mActionOverflowAdapter;

  private int mCurCount;

  private int mOverflowCurCount;

  private final Paint mTopLinePaint = new Paint();
  private final Paint mBotLinePaint = new Paint();

  public TopBar(Context context) {
    this(context, null);
  }

  public TopBar(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.topBarStyle);
  }

  public TopBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    final float density = getResources().getDisplayMetrics().density;

    int minimumTitleWidth = (int) (MINIMIUM_TITLE_WIDTH * density + 0.5f);

    View.inflate(context, R.layout.widget_top_bar, this);
    U.viewBinding(this, this);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar, defStyle, 0);

    mTitle.setTextColor(ta.getColor(R.styleable.TopBar_titleColor, Color.GRAY));

    mTopLinePaint.setColor(ta.getColor(R.styleable.TopBar_topLineColor, Color.GRAY));
    mBotLinePaint.setColor(ta.getColor(R.styleable.TopBar_botLineColor, Color.GRAY));

    mTopLinePaint.setStrokeWidth(1);
    mBotLinePaint.setStrokeWidth(1);

    mActionOverFlow.setOnClickListener(this);

    mActionLeft.setOnClickListener(this);
  }

  public String getTitle() {
    if (mTitle != null) {
      return mTitle.getText().toString();
    }
    return null;
  }

  public void setTitle(String title) {
    if (mTitle != null) {
      mTitle.setText(title);
    }
  }

  public EditText getSearchEditText() {
    return mEtSearch;
  }

  public String getSubTitle() {
    return mSubTitle.getText().toString();
  }

  public void setSubTitle(String subTitle) {
    mSubTitle.setText(subTitle);
  }

  public void setActionOverflowAdapter(ActionAdapter actionAdapter) {
    mActionOverflowAdapter = actionAdapter;
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
      View view;
      if (TextUtils.isEmpty(mActionAdapter.getTitle(i))) {
        view = buildActionItemView(i, mActionAdapter.getIcon(i), mActionAdapter.getBackgroundDrawable(i));
      } else {
        view = buildActionItemView(i, mActionAdapter.getTitle(i), mActionAdapter.getBackgroundDrawable(i));
      }
      addView(view);
      mActionViews.add(view);
    }
    requestLayout();
    invalidate();
  }

  public void setOnActionOverflowClickListener(OnClickListener onClickListener) {
    mOnActionOverflowClickListener = onClickListener;
  }

  public void setOnActionLeftClickListener(OnClickListener onClickListener) {
    mOnActionLeftClickListener = onClickListener;
  }

  public void enterSearch() {
    mActionOverFlow.setVisibility(INVISIBLE);
    for (View v : mActionViews) {
      v.setVisibility(INVISIBLE);
    }

    mFlSearch.setVisibility(VISIBLE);
  }

  public void exitSearch() {
    if (mActionOverflowAdapter != null && mActionOverflowAdapter.getCount() > 0) {
      mActionOverFlow.setVisibility(VISIBLE);
    }
    for (View v : mActionViews) {
      v.setVisibility(VISIBLE);
    }

    mFlSearch.setVisibility(INVISIBLE);
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
    if (mOnActionLeftClickListener != null) {
      mOnActionLeftClickListener.onClick(v);
    }
  }

  @OnClick (R.id.tb_iv_action_overflow)
  public void onActionOverflowClicked(View v) {
    if (mOnActionOverflowClickListener != null) {
      mOnActionOverflowClickListener.onClick(v);
    }
  }

  @SuppressWarnings ("SuspiciousNameCombination")
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    final int widthSize = widthMeasureSpec & ~(0x3 << 30);

    int widthLeft = widthSize;

    measureChild(mRlTitle, widthSize + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    widthLeft -= mRlTitle.getMeasuredWidth();

    mFlSearch.measure(widthSize - mIvAppIcon.getRight() + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

    if (mActionOverflowAdapter != null) {
      if (mOverflowCurCount != mActionOverflowAdapter.getCount()) {
        throw new IllegalStateException("Adapter count updates");
      }
      if (mOverflowCurCount != 0) {
        mActionOverFlow.measure(heightSize + MeasureSpec.EXACTLY, widthSize + MeasureSpec.EXACTLY);
      } else {
        mActionOverFlow.measure(MeasureSpec.EXACTLY, widthSize + MeasureSpec.EXACTLY);
      }
    } else {
      mActionOverFlow.measure(MeasureSpec.EXACTLY, widthSize + MeasureSpec.EXACTLY);
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
            view.measure(heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.AT_MOST);
          }
          widthLeft -= view.getMeasuredWidth();
        }
      }
    }

    setMeasuredDimension(widthSize, heightSize);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    int right = r - U.dp2px(DEFAULT_RIGHT_PADDING);
    final int height = b - t;

    mRlTitle.layout(0, 0, mRlTitle.getMeasuredWidth(), b);

    mFlSearch.layout(mActionLeft.getRight(), 0, mActionLeft.getRight() + mFlSearch.getMeasuredWidth(), b);

    mActionOverFlow.layout(right - mActionOverFlow.getMeasuredWidth(), 0, right, b);

    right -= mActionOverFlow.getMeasuredWidth();

    if (mCurCount != 0) {
      for (View child : mActionViews) {
        child.layout(right - child.getMeasuredWidth(),
            (height - child.getMeasuredHeight()) >> 1,
            right,
            (height + child.getMeasuredHeight()) >> 1);
        right -= child.getMeasuredWidth();
      }
    }
  }

  private View buildActionItemView(final int position, Drawable drawable, Drawable backgroundDrawable) {
    if (drawable == null) return null;

    final ImageView imageView = new ImageView(getContext());
    imageView.setImageDrawable(drawable);
    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    imageView.setBackgroundDrawable(backgroundDrawable);
    imageView.setOnClickListener(this);

    return imageView;
  }

  private View buildActionItemView(final int position, String text, Drawable backgroundDrawable) {
    if (TextUtils.isEmpty(text)) return null;

    final TextView textView = new TextView(getContext());
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    textView.setText(text);
    textView.setGravity(Gravity.CENTER);
    textView.setTextColor(Color.WHITE);
    textView.setBackgroundDrawable(backgroundDrawable);
    textView.setOnClickListener(this);
    final int padding = U.dp2px(5);
    textView.setPadding(padding, padding, padding, padding);
    return textView;
  }

  public interface ActionAdapter {
    String getTitle(int position);

    Drawable getIcon(int position);

    Drawable getBackgroundDrawable(int position);

    void onClick(View view, int position);

    int getCount();
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    // draw top line
    canvas.drawLine(0, 0, getMeasuredWidth(), 0, mTopLinePaint);
    canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, mBotLinePaint);
  }
}
