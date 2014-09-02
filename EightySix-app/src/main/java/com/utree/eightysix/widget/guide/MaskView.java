package com.utree.eightysix.widget.guide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import de.akquinet.android.androlog.Log;

/**
 * @author Simon
 */
class MaskView extends ViewGroup {
	private static final String TAG = "MaskView";

	private final RectF mTargetRect = new RectF();
	private final RectF mFullingRect = new RectF();
	private final RectF mChildTmpRect = new RectF();
	private final Paint mFullingPaint = new Paint();

	private final Path mOutPath = new Path();

	private boolean mCustomFullingRect;

	public MaskView(Context context) {
		this(context, null, 0);
	}

	public MaskView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MaskView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setLayoutParams(new ViewGroup.LayoutParams(FILL_PARENT, FILL_PARENT));
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

		mOutPath.setFillType(Path.FillType.EVEN_ODD);
		resetOutPath();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		clearFocus();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int w = widthMeasureSpec & ~(0x3 << 30);
		final int h = heightMeasureSpec & ~(0x3 << 30);
		setMeasuredDimension(w, h);
		if (!mCustomFullingRect) {
			mFullingRect.set(0, 0, w, h);
			resetOutPath();
		}

		final int count = getChildCount();
		View child;
		for (int i = 0; i < count; i++) {
			child = getChildAt(i);
			if (child != null) {
				LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp == null) {
					child.setLayoutParams(null);
				}
				measureChild(child, w + MeasureSpec.AT_MOST, h + MeasureSpec.AT_MOST);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		final float density = getResources().getDisplayMetrics().density;
		View child;
		for (int i = 0; i < count; i++) {
			child = getChildAt(i);
			if (child == null) continue;
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			if (lp == null) continue;
			switch (lp.targetAnchor) {
			case LayoutParams.ANCHOR_LEFT:
				mChildTmpRect.right = mTargetRect.left;
				mChildTmpRect.left = mChildTmpRect.right - child.getMeasuredWidth();
				verticalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
				break;
			case LayoutParams.ANCHOR_TOP:
				mChildTmpRect.bottom = mTargetRect.top;
				mChildTmpRect.top = mChildTmpRect.bottom - child.getMeasuredHeight();
				horizontalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
				break;
			case LayoutParams.ANCHOR_RIGHT:
				mChildTmpRect.left = mTargetRect.right;
				mChildTmpRect.right = mChildTmpRect.left + child.getMeasuredWidth();
				verticalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
				break;
			case LayoutParams.ANCHOR_BOTTOM:
				mChildTmpRect.top = mTargetRect.bottom;
				mChildTmpRect.bottom = mChildTmpRect.top + child.getMeasuredHeight();
				horizontalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
				break;
			case LayoutParams.ANCHOR_OVER:
				mChildTmpRect.left = ((int) mTargetRect.width() - child.getMeasuredWidth()) >> 1;
				mChildTmpRect.top = ((int) mTargetRect.height() - child.getMeasuredHeight()) >> 1;
				mChildTmpRect.right = ((int) mTargetRect.width() + child.getMeasuredWidth()) >> 1;
				mChildTmpRect.bottom = ((int) mTargetRect.height() + child.getMeasuredHeight()) >> 1;
				mChildTmpRect.offset(mTargetRect.left, mTargetRect.top);
				break;
			}
			mChildTmpRect.offset((int) (density * lp.offsetX + 0.5f),
					(int) (density * lp.offsetY + 0.5f));
			Log.d(TAG, "onLayout child layout to: " + mChildTmpRect);
			child.layout((int) mChildTmpRect.left, (int) mChildTmpRect.top,
					(int) mChildTmpRect.right, (int) mChildTmpRect.bottom);
		}
	}

	private void horizontalChildPositionLayout(View child, RectF rect, int targetParentPosition) {
		switch (targetParentPosition) {
		case LayoutParams.PARENT_START:
			rect.left = mTargetRect.left;
			rect.right = rect.left + child.getMeasuredWidth();
			break;
		case LayoutParams.PARENT_CENTER:
			rect.left = (mTargetRect.width() - child.getMeasuredWidth()) / 2;
			rect.right = (mTargetRect.width() + child.getMeasuredWidth()) / 2;
			rect.offset(mTargetRect.left, 0);
			break;
		case LayoutParams.PARENT_END:
			rect.right = mTargetRect.right;
			rect.left = rect.right - child.getMeasuredWidth();
			break;
		}
	}

	private void verticalChildPositionLayout(View child, RectF rect, int targetParentPosition) {
		switch (targetParentPosition) {
		case LayoutParams.PARENT_START:
			rect.top = mTargetRect.top;
			rect.bottom = rect.top + child.getMeasuredHeight();
			break;
		case LayoutParams.PARENT_CENTER:
			rect.top = (mTargetRect.width() - child.getMeasuredHeight()) / 2;
			rect.bottom = (mTargetRect.width() + child.getMeasuredHeight()) / 2;
			rect.offset(0, mTargetRect.top);
			break;
		case LayoutParams.PARENT_END:
			rect.bottom = mTargetRect.bottom;
			rect.top = mTargetRect.bottom - child.getMeasuredHeight();
			break;
		}
	}

	private void resetOutPath() {
		mOutPath.reset();
		mOutPath.addRect(mTargetRect, Path.Direction.CW);
		Log.d(TAG, "resetOutPath target rect = " + mTargetRect);
		mOutPath.addRect(mFullingRect, Path.Direction.CW);
		Log.d(TAG, "resetOutPath fulling rect = " + mFullingRect);
	}

	public void setTargetRect(Rect rect) {
		mTargetRect.set(rect);
		resetOutPath();
		Log.d(TAG, "settargetRect target rect = " + mTargetRect);
		invalidate();
	}

	public void setFullingRect(Rect rect) {
		mFullingRect.set(rect);
		resetOutPath();
		Log.d(TAG, "setFullingRect fulling rect = " + mFullingRect);
		mCustomFullingRect = true;
		invalidate();
	}

	public void setFullingAlpha(int alpha) {
		mFullingPaint.setAlpha(alpha);
		invalidate();
	}

	public void setFullingColor(int color) {
		mFullingPaint.setColor(color);
		invalidate();
	}

	public void setOverlayTarget(boolean b) {
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
	}

	private final Paint mTargetPaint = new Paint();
	private final Paint mPaint = new Paint();

	{
		mPaint.setAntiAlias(true);
		mTargetPaint.setColor(0xFFFF0000);
		mTargetPaint.setStrokeWidth(10);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		final long drawingTime = getDrawingTime();
    //canvas.clipRect(mTargetRect, Region.Op.DIFFERENCE);
		canvas.drawRect(mFullingRect, mFullingPaint);

		try {
			View child;
			for (int i = 0; i < getChildCount(); i++) {
				child = getChildAt(i);
				drawChild(canvas, child, drawingTime);
			}
		} catch (NullPointerException ignored) {

		}

	}

	static class LayoutParams extends ViewGroup.LayoutParams {
		public static final int ANCHOR_LEFT = 0x01;
		public static final int ANCHOR_TOP = 0x02;
		public static final int ANCHOR_RIGHT = 0x03;
		public static final int ANCHOR_BOTTOM = 0x04;
		public static final int ANCHOR_OVER = 0x05;

		public static final int PARENT_START = 0x10;
		public static final int PARENT_CENTER = 0x20;
		public static final int PARENT_END = 0x30;

		public int targetAnchor = ANCHOR_BOTTOM;
		public int targetParentPosition = PARENT_CENTER;
		public int offsetX = 0;
		public int offsetY = 0;

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
