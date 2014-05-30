package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    private final int mMinimiumTitleWidth;
    private final List<View> mActionViews = new ArrayList<View>();

    @ViewId(R.id.top_bar_title)
    public TextView mTitle;

    @ViewId(R.id.top_bar_progress)
    public ProgressBar mProgressBar;

    @ViewId(R.id.top_bar_action_overflow)
    @OnClick
    public ImageView mActionOverFlow;

    @ViewId(R.id.top_bar_left_action)
    @OnClick
    public ImageView mActionLeft;

    public OnClickListener mOnActionOverflowClickListener;
    private ActionAdapter mActionAdapter;
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

        mMinimiumTitleWidth = (int) (MINIMIUM_TITLE_WIDTH * density + 0.5f);

        View.inflate(context, R.layout.widget_top_bar, this);
        U.viewBinding(this, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar, defStyle, 0);

        mTitle.setTextColor(ta.getColor(R.styleable.TopBar_titleColor, Color.GRAY));

        mActionBgDrawableId = ta.getResourceId(R.styleable.TopBar_actionBgSelector, 0);
        mActionOverFlow.setBackgroundDrawable(getResources().getDrawable(mActionBgDrawableId));
        mActionOverFlow.setOnClickListener(this);

        mProgressBar.setVisibility(GONE);
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

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(GONE);
        }
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
            View view = buildActionItemView(i, mActionAdapter.getIcon(i));
            addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            mActionViews.add(view);
        }
        requestLayout();
        invalidate();
    }

    public void setOnActionOverflowClickListener(OnClickListener onClickListener) {
        mOnActionOverflowClickListener = onClickListener;
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

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int heightSize = heightMeasureSpec & ~(0x3 << 30);

        final int widthSize = widthMeasureSpec & ~(0x3 << 30);

        int widthLeft = widthSize;

        measureChild(mActionLeft, heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);

        widthLeft -= mActionLeft.getMeasuredWidth();

        // keep actions as square
        if (mActionAdapter != null) {
            if (mCurCount != mActionAdapter.getCount()) {
                throw new IllegalStateException("Adapter count updates");
            }
            if (mCurCount == 0) {
                mActionOverFlow.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
            } else {
                mActionOverFlow.measure(heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);
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

        if (mProgressBar.getVisibility() == GONE || widthLeft < heightSize) {
            mProgressBar.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
        } else {
            mProgressBar.measure(heightSize + MeasureSpec.EXACTLY, heightSize + MeasureSpec.EXACTLY);
        }
        widthLeft -= mProgressBar.getMeasuredWidth();

        mTitle.measure(widthLeft + MeasureSpec.AT_MOST, heightSize + MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int right = r;

        mActionLeft.layout(0, 0, mActionLeft.getMeasuredWidth(), b);

        mTitle.layout(mActionLeft.getRight(), 0, mActionLeft.getRight() + mTitle.getMeasuredWidth(), b);

        mActionOverFlow.layout(right - mActionOverFlow.getMeasuredWidth(), 0, right, b);

        right -= mActionOverFlow.getMeasuredWidth();

        mProgressBar.layout(right - mProgressBar.getMeasuredWidth(), 0, right, b);

        right -= mProgressBar.getMeasuredWidth();

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
        imageView.setBackgroundDrawable(getResources().getDrawable(mActionBgDrawableId));
        imageView.setOnClickListener(this);

        return imageView;
    }

    public interface ActionAdapter {
        String getTitle(int position);

        Drawable getIcon(int position);

        void onClick(View view, int position);

        int getCount();
    }

}
