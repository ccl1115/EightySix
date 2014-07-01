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

  private final List<ActionButton> mActionViews = new ArrayList<ActionButton>();

  @InjectView (R.id.tb_tv_bar_title)
  public TextView mTitle;

  @InjectView (R.id.tb_tv_sub_title)
  public TextView mSubTitle;

  @InjectView (R.id.tb_iab_action_overflow)
  public ImageActionButton mActionOverFlow;

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

  private int mCurCount;

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
      if (layoutParams == null) layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

  public void setOnActionOverflowClickListener(OnClickListener onClickListener) {
    mOnActionOverflowClickListener = onClickListener;
    requestLayout();
    invalidate();
  }

  public void setOnActionLeftClickListener(OnClickListener onClickListener) {
    mOnActionLeftClickListener = onClickListener;
  }

  public void enterSearch() {
    mFlSearch.setVisibility(VISIBLE);
  }

  public void exitSearch() {
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

  @OnClick (R.id.tb_iab_action_overflow)
  public void onActionOverflowClicked(View v) {
    if (mOnActionOverflowClickListener != null) {
      mOnActionOverflowClickListener.onClick(v);
    }
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

    if (mOnActionOverflowClickListener != null) {
      mActionOverFlow.measure(heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);
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
                childWidthSpec = heightSize + MeasureSpec.AT_MOST;
                break;
              case LayoutParams.MATCH_PARENT:
                childWidthSpec = heightSize + MeasureSpec.EXACTLY;
                break;
              default:
                childWidthSpec = lp.width + MeasureSpec.EXACTLY;
            }
            view.measure(childWidthSpec, childHeightSpec);
          }
          widthLeft -= view.getMeasuredWidth();
        }
      }
    }

    measureChild(mRlTitle, widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

    mFlSearch.measure(widthSize - mIvAppIcon.getRight() + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);


    setMeasuredDimension(widthSize, heightSize);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    final int height = b - t;

    mRlTitle.layout(0, 0, mRlTitle.getMeasuredWidth(), b);

    mFlSearch.layout(mIvAppIcon.getRight(), 0, mIvAppIcon.getRight() + mFlSearch.getMeasuredWidth(), b);

    mActionOverFlow.layout(r - mActionOverFlow.getMeasuredWidth(), 0, r, b);

    r -= mActionOverFlow.getMeasuredWidth();

    if (mCurCount != 0) {
      for (View child : mActionViews) {
        r -= height;
        child.layout(r + ((height - child.getMeasuredWidth()) >> 1),
            (height - child.getMeasuredHeight()) >> 1,
            r + ((height + child.getMeasuredWidth()) >> 1),
            (height + child.getMeasuredHeight()) >> 1);
      }
    }
  }

  private ActionButton buildActionItemView(Drawable drawable, Drawable backgroundDrawable, LayoutParams layoutParams) {
    if (drawable == null) return null;

    final ImageActionButton imageView = new ImageActionButton(getContext());
    imageView.setImageDrawable(drawable);
    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    imageView.setActionLayoutParams((FrameLayout.LayoutParams) layoutParams);
    imageView.setActionBackgroundDrawable(backgroundDrawable);
    imageView.setOnClickListener(this);

    return imageView;
  }

  private ActionButton buildActionItemView(String text, Drawable backgroundDrawable, LayoutParams layoutParams) {
    if (TextUtils.isEmpty(text)) return null;

    final TextActionButton button = new TextActionButton(getContext());
    button.setActionLayoutParams((FrameLayout.LayoutParams) layoutParams);
    button.setActionBackgroundDrawable(backgroundDrawable);
    button.setTextSize(14);
    button.setText(text);
    button.setGravity(Gravity.CENTER);
    button.setTextColor(Color.WHITE);
    button.setSingleLine(true);
    button.setLines(1);
    button.setOnClickListener(this);
    final int hPadding = U.dp2px(6);
    final int vPadding = U.dp2px(4);
    button.setActionPadding(hPadding, vPadding, hPadding, vPadding);
    return button;
  }

  public interface ActionAdapter {
    String getTitle(int position);

    Drawable getIcon(int position);

    Drawable getBackgroundDrawable(int position);

    void onClick(View view, int position);

    int getCount();

    FrameLayout.LayoutParams getLayoutParams(int position);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    // draw top line
    canvas.drawLine(0, 0, getMeasuredWidth(), 0, mTopLinePaint);
    canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, mBotLinePaint);
  }
}
