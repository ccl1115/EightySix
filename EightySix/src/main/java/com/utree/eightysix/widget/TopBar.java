package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TopBar extends ViewGroup implements View.OnClickListener {

    private static final int MINIMIUM_TITLE_WIDTH = 60;

    private final List<View> mActionViews = new ArrayList<View>();

    @ViewId (R.id.top_bar_title)
    public TextView mTitle;

    @ViewId (R.id.top_bar_sub_title)
    public TextView mSubTitle;

    @ViewId (R.id.top_bar_action_overflow)
    @OnClick
    public ImageView mActionOverFlow;

    @ViewId (R.id.top_bar_action_left)
    @OnClick
    public ImageView mActionLeft;

    @ViewId (R.id.top_bar_search)
    public FrameLayout mFlSearch;

    @ViewId (R.id.top_bar_iv_search_close)
    @OnClick
    public ImageView mIvSearchClose;

    @ViewId (R.id.top_bar_et_search)
    public EditText mEtSearch;

    private OnClickListener mOnActionOverflowClickListener;
    private OnClickListener mOnActionLeftClickListener;

    private ActionAdapter mActionAdapter;
    private ActionAdapter mAtionOverflowAdapter;

    private int mCurCount;
    private int mActionBgDrawableId;

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

        mActionBgDrawableId = ta.getResourceId(R.styleable.TopBar_actionBgSelector, 0);
        if (mActionBgDrawableId != 0) {
            mActionOverFlow.setBackgroundDrawable(getResources().getDrawable(mActionBgDrawableId));
        }
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

    public void showProgressBar() {
    }

    public void hideProgressBar() {
    }

    public void setAtionOverflowAdapter(ActionAdapter actionAdapter) {
        mAtionOverflowAdapter = actionAdapter;
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
                view = buildActionItemView(i, mActionAdapter.getIcon(i));
            } else {
                view = buildActionItemView(i, mActionAdapter.getTitle(i));
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
        if (mAtionOverflowAdapter != null && mAtionOverflowAdapter.getCount() > 0) {
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
            case R.id.top_bar_action_overflow:
                if (mOnActionOverflowClickListener != null) {
                    mOnActionOverflowClickListener.onClick(v);
                }
                break;
            case R.id.top_bar_action_left:
                if (mOnActionLeftClickListener != null) {
                    mOnActionLeftClickListener.onClick(v);
                }
                break;
            case R.id.top_bar_iv_search_close:
                mEtSearch.setText("");
                break;
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

    @SuppressWarnings ("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int heightSize = heightMeasureSpec & ~(0x3 << 30);

        final int widthSize = widthMeasureSpec & ~(0x3 << 30);

        int widthLeft = widthSize;

        measureChild(mActionLeft, heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

        widthLeft -= mActionLeft.getMeasuredWidth();

        mFlSearch.measure(widthLeft + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

        // keep actions as square
        if (mActionAdapter != null) {
            if (mCurCount != mActionAdapter.getCount()) {
                throw new IllegalStateException("Adapter count updates");
            }
            if (mCurCount != 0) {
                widthLeft -= mActionOverFlow.getMeasuredWidth();

                for (View view : mActionViews) {
                    if (widthLeft < heightSize) {
                        view.measure(MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);
                    } else {
                        view.measure(heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);
                    }
                    widthLeft -= view.getMeasuredWidth();
                }
            }
        }

        mTitle.measure(widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int right = r;

        mActionLeft.layout(0, 0, mActionLeft.getMeasuredWidth(), b);

        mFlSearch.layout(mActionLeft.getRight(), 0, mActionLeft.getRight() + mFlSearch.getMeasuredWidth(), b);

        mTitle.layout(mActionLeft.getRight(), 0, mActionLeft.getRight() + mTitle.getMeasuredWidth(), b);

        mActionOverFlow.layout(right - mActionOverFlow.getMeasuredWidth(), 0, right, b);

        right -= mActionOverFlow.getMeasuredWidth();

        if (mCurCount != 0) {
            for (View child : mActionViews) {
                child.layout(right - child.getMeasuredWidth(), 0, right, b);
                right -= child.getMeasuredWidth();
            }
        }
    }

    private View buildActionItemView(final int position, Drawable drawable) {
        if (drawable == null) return null;

        final ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        if (mActionBgDrawableId != 0) {
            imageView.setBackgroundDrawable(getResources().getDrawable(mActionBgDrawableId));
        }
        imageView.setOnClickListener(this);

        return imageView;
    }

    private View buildActionItemView(final int position, String text) {
        if (TextUtils.isEmpty(text)) return null;

        final TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        if (mActionBgDrawableId != 0) {
            textView.setBackgroundDrawable(getResources().getDrawable(mActionBgDrawableId));
        }
        textView.setOnClickListener(this);
        return textView;
    }

    public interface ActionAdapter {
        String getTitle(int position);

        Drawable getIcon(int position);

        void onClick(View view, int position);

        int getCount();
    }

}
