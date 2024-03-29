package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.utree.eightysix.R;
import de.akquinet.android.androlog.Log;

/**
 */
@SuppressWarnings ("unused")
public class RefresherView extends ViewGroup implements IRefreshable {
  private static final String TAG = "RefresherView";

  private static final String DIRECTION_TOP = "top";
  private static final String DIRECTION_SIDE = "side";

  private static final int MSG_ANIMATE_BACK = 1000;
  private static final int MSG_ANIMATE_DOWN = 1001;

  private static final int MSG_HEADER_INVALIDATE = 1002;

  private static final int DEFAULT_THRESHOLD_HEIGHT = 200; // dips
  private static final int DEFAULT_MAX_HEIGHT = 400; // dips

  private static final int MIN_VELOCITY = 100;

  private final int kMinVelocity;
  private final int kVelocity;
  private final int[] mContentLocation = new int[2];
  private final int[] mTempLocation = new int[2];
  private int mThresholdHeight;
  private int mMaxHeight;
  private int mRefresherContentId;
  private int mRefresherHeaderId;
  private int mEmptyViewId;
  private View mRefresherContent;
  private View mRefresherHeader;
  private View mEmptyView;
  private boolean mEnable = true;
  private boolean mRefreshing;
  private int mLastDownY;
  private int mLastDownX;
  private int mAbsY;
  private int mAbsX;
  private int mYOffset;
  private int mXOffset;
  private int mBackPosition;
  private Animator mAnimator;
  private AnimatorHandler mHandler;
  private OnRefreshListener mOnRefreshListener;

  private ViewGroupDelegate mViewGroupDelegate;

  private State mState = State.idle;

  public RefresherView(Context context) {
    this(context, null, 0);
  }

  public RefresherView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RefresherView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mAnimator = new Animator();
    mHandler = new AnimatorHandler();

    final Resources r = getResources();
    final float density = r.getDisplayMetrics().density;

    kMinVelocity = (int) (MIN_VELOCITY * density + 0.5f);
    kVelocity = (int) (ViewConfig.VELOCITY_SMALL * density + 0.5f);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefresherView);

    mThresholdHeight = ta.getDimensionPixelOffset(R.styleable.RefresherView_threshold_height, -1);
    if (mThresholdHeight == -1) {
      mThresholdHeight = (int) (DEFAULT_THRESHOLD_HEIGHT * density + 0.5f);
    }

    mMaxHeight = ta.getDimensionPixelOffset(R.styleable.RefresherView_max_height, -1);
    if (mMaxHeight == -1) {
      mMaxHeight = (int) (DEFAULT_MAX_HEIGHT * density + 0.5f);
    }

    String direction = ta.getString(R.styleable.RefresherView_direction);
    if (direction.equals(DIRECTION_SIDE)) {
      mViewGroupDelegate = new SideRefreshViewGroupDelegate();
    } else if (direction.equals(DIRECTION_TOP)) {
      mViewGroupDelegate = new TopRefreshViewGroupDelegate();
    } else {
      mViewGroupDelegate = new TopRefreshViewGroupDelegate();
    }

    mRefresherContentId = ta.getResourceId(R.styleable.RefresherView_refresher_content, -1);
    mRefresherHeaderId = ta.getResourceId(R.styleable.RefresherView_refresher_head, -1);
    mEmptyViewId = ta.getResourceId(R.styleable.RefresherView_empty_view, -1);
  }

  @Override
  @SuppressWarnings ("all")
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return mViewGroupDelegate.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return mViewGroupDelegate.interceptionTouchEvent(ev);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    mViewGroupDelegate.draw(canvas);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    mViewGroupDelegate.layout(changed, l, t, r, b);
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new MarginLayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateLayoutParams(LayoutParams p) {
    return p;
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  public void setOnRefreshListener(OnRefreshListener listener) {
    mOnRefreshListener = listener;
  }

  @Override
  public void setEnable(boolean enable) {
    // Can be true only when the refresher content and header are not null.
    mEnable = enable && mRefresherContent != null && mRefresherHeader != null;
  }

  @Override
  public State getState() {
    return mState;
  }

  public void refresh() {
    mBackPosition = mThresholdHeight;
    mRefreshing = true;
    if (mOnRefreshListener != null) {
      mOnRefreshListener.onPreRefresh();
    }
    mHandler.sendEmptyMessageAtTime(MSG_HEADER_INVALIDATE, System.currentTimeMillis() + ViewConfig.ANIMATION_FRAME_DURATION);
  }

  @Override
  public void showHeader() {
    if (!mRefreshing) mViewGroupDelegate.animate(MSG_ANIMATE_DOWN);
  }

  @Override
  public boolean isEnabled() {
    return mEnable;
  }

  @Override
  public final boolean onTouchEvent(final MotionEvent event) {
    return mViewGroupDelegate.touchEvent(event);
  }

  @Override
  @SuppressWarnings ("unchecked")
  protected void onFinishInflate() {
    if (mRefresherContentId == -1) {
      throw new RuntimeException("refresher content id is not set in xml, or call setRefresherContent before add it to a view tree.");
    } else {
      mRefresherContent = findViewById(mRefresherContentId);
      if (mRefresherContent == null) {
        throw new RuntimeException("refresher content not found in the view tree by the content id.");
      }
    }

    if (mRefresherHeaderId == -1) {
      throw new RuntimeException("refresher head id is not set in xml, or call setRefresherHeader before add it to a view tree.");
    } else {
      mRefresherHeader = findViewById(mRefresherHeaderId);
      if (mRefresherHeader == null) {
        throw new RuntimeException("refresher header not found in the view tree by the header id.");
      }
    }

    if (mEmptyViewId == -1) {
      throw new RuntimeException("empty view id is not set in xml, or call setEmptyView before add it to a view tree");
    } else {
      mEmptyView = findViewById(mEmptyViewId);
      if (mEmptyView == null) {
        throw new RuntimeException("empty view not found in the view tree by the empty view's id");
      }
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    mViewGroupDelegate.measure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public View getRefresherContent() {
    return mRefresherContent;
  }

  public void hideHeader() {
    mBackPosition = 0;
    mViewGroupDelegate.animate(MSG_ANIMATE_BACK);
    mHandler.removeMessages(MSG_HEADER_INVALIDATE);
  }

  @Override
  public View getRefresherHeader() {
    return mRefresherHeader;
  }

  private class Animator {
    private boolean animating;
    private long lastAnimationTime;
    private long currentAnimatingTime;
    private int animatingVelocity;
    private int animatingPosition;
    private int animationDistance;

    void computeBack() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - lastAnimationTime) / 1000f;

      animatingPosition += animatingVelocity * t;

      if (animatingPosition >= animationDistance) {
        mYOffset = mBackPosition;
        animating = false;
        mState = State.idle;
        final OnRefreshListener onRefreshListener = mOnRefreshListener;
        if (onRefreshListener != null) {
          onRefreshListener.onStateChanged(State.idle);
        }

        if (mBackPosition == 0) {
          if (onRefreshListener != null) {
            onRefreshListener.onRefreshUI();
            mRefreshing = false;
          }
        }
      } else {
        mYOffset = (int)
            (mBackPosition + animationDistance * (1 - ViewConfig.sInterpolator.getInterpolation(
                animatingPosition / (float) animationDistance)));
        lastAnimationTime = now;
        currentAnimatingTime = now + ViewConfig.ANIMATION_FRAME_DURATION;
        mHandler.removeMessages(MSG_ANIMATE_BACK);
        mHandler.sendEmptyMessageAtTime(MSG_ANIMATE_BACK, currentAnimatingTime);
      }

      invalidate();
    }

    void computeDown() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - lastAnimationTime) / 1000f;

      animatingPosition += animatingVelocity * t;

      if (animatingPosition >= animationDistance) {
        mYOffset = mThresholdHeight;
        animating = false;
        mState = State.idle;
        final OnRefreshListener onRefreshListener = mOnRefreshListener;
        if (onRefreshListener != null) {
          onRefreshListener.onStateChanged(State.idle);
          refresh();
        }
      } else {
        mYOffset = ViewConfig.computeInterpolator(animationDistance, animatingPosition, false);
        lastAnimationTime = now;
        currentAnimatingTime = now + ViewConfig.ANIMATION_FRAME_DURATION;
        mHandler.removeMessages(MSG_ANIMATE_DOWN);
        mHandler.sendEmptyMessageAtTime(MSG_ANIMATE_DOWN, currentAnimatingTime);
      }

      invalidate();
    }

    void animate(final int msg) {
      Log.d(TAG, "@animate");
      final long now = SystemClock.uptimeMillis();
      final OnRefreshListener onRefreshListener;
      lastAnimationTime = now;
      currentAnimatingTime = now + ViewConfig.ANIMATION_FRAME_DURATION;
      animating = true;

      switch (msg) {
        case MSG_ANIMATE_BACK:
          animationDistance = mYOffset - mBackPosition;
          animatingPosition = 0;
          animatingVelocity = Math.max(kMinVelocity, (mYOffset - mBackPosition) * 2);
          mHandler.removeMessages(MSG_ANIMATE_BACK);
          mHandler.sendEmptyMessageAtTime(MSG_ANIMATE_BACK, currentAnimatingTime);
          break;
        case MSG_ANIMATE_DOWN:
          animationDistance = mThresholdHeight;
          animatingPosition = 0;
          animatingVelocity = kVelocity;
          mHandler.removeMessages(MSG_ANIMATE_DOWN);
          mHandler.sendEmptyMessageAtTime(MSG_ANIMATE_DOWN, currentAnimatingTime);
          break;
      }

      mState = State.animating;
      onRefreshListener = mOnRefreshListener;
      if (onRefreshListener != null) {
        onRefreshListener.onStateChanged(State.animating);
      }
    }

  }

  @Override
  public View getEmptyView() {
    return mEmptyView;
  }

  private class AnimatorHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      if (msg.what == MSG_ANIMATE_BACK) {
        mAnimator.computeBack();
      } else if (msg.what == MSG_ANIMATE_DOWN) {
        mAnimator.computeDown();
      } else if (msg.what == MSG_HEADER_INVALIDATE) {
        invalidate(0, 0, getRight(), mRefresherHeader.getMeasuredHeight());
        sendEmptyMessageAtTime(MSG_HEADER_INVALIDATE, System.currentTimeMillis() + ViewConfig.ANIMATION_FRAME_DURATION);
      }
    }
  }

  @Override
  public void setRefresherContent(ViewGroup view) {
    removeView(mRefresherContent);
    mRefresherContent = view;
    if (mRefresherContent == null) {
      mEnable = false;
    } else {
      addView(mRefresherContent);
      mEnable = mRefresherHeader != null && mRefresherContent != null;
    }

  }

  private class TopRefreshViewGroupDelegate implements ViewGroupDelegate {

    @Override
    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
      final int widthSize = widthMeasureSpec & ~(0x3 << 30);
      final int heightSize = heightMeasureSpec & ~(0x3 << 30);
      if (mRefresherContent != null) {
        measureChild(mRefresherContent, widthSize + MeasureSpec.EXACTLY,
            heightSize + MeasureSpec.EXACTLY);
      }

      if (mEmptyView != null) {
        measureChild(mEmptyView, widthSize + MeasureSpec.AT_MOST,
            heightSize + MeasureSpec.AT_MOST);
      }

      if (mRefresherHeader != null) {
        measureChild(mRefresherHeader, widthSize + MeasureSpec.EXACTLY,
            heightSize + MeasureSpec.AT_MOST);
      }

      setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void layout(boolean changed, int l, int t, int r, int b) {
      final int width = r - l;
      final int height = b - t;
      if (mRefresherContent != null) {
        mRefresherContent.layout(0, 0, width, height);
      }

      if (mEmptyView != null) {
        mEmptyView.layout((width - mEmptyView.getMeasuredWidth()) / 2,
            (height - mEmptyView.getMeasuredHeight()) / 2,
            (width + mEmptyView.getMeasuredWidth()) / 2,
            (height + mEmptyView.getMeasuredHeight()) / 2);
      }

      if (mRefresherHeader != null) {
        MarginLayoutParams mlp = (MarginLayoutParams) mRefresherHeader.getLayoutParams();
        mRefresherHeader.layout(mlp.leftMargin, -(mRefresherHeader.getMeasuredHeight() + mlp.bottomMargin),
            r - mlp.rightMargin, -mlp.bottomMargin);
      }

      getLocationOnScreen(mTempLocation);
      mAbsY = mTempLocation[1];
    }

    @Override
    public void draw(Canvas canvas) {
      final long drawingTime = getDrawingTime();

      if (mEmptyView.getVisibility() == VISIBLE) {
        drawChild(canvas, mEmptyView, drawingTime);
      }

      canvas.save();
      canvas.translate(0, mYOffset / 2);
      drawChild(canvas, mRefresherContent, drawingTime);
      if (mYOffset > 0) {
        if (mRefresherHeader.getVisibility() == VISIBLE) {
          drawChild(canvas, mRefresherHeader, drawingTime);
        }
      }
      canvas.restore();
    }

    @Override
    @SuppressWarnings ("all")
    public boolean dispatchTouchEvent(MotionEvent event) {
      return RefresherView.super.dispatchTouchEvent(event) || true;
    }

    @Override
    public boolean interceptionTouchEvent(MotionEvent ev) {
      if (!mEnable || mRefreshing) {
        return false;
      }

      final int action = ev.getAction() & MotionEvent.ACTION_MASK;
      final int y = (int) ev.getY();

      switch (action) {
        case MotionEvent.ACTION_DOWN:
          mLastDownY = y;

          mHandler.removeMessages(MSG_ANIMATE_BACK);
          break;

        case MotionEvent.ACTION_MOVE:
          View childAt;
          if (mRefresherContent instanceof ViewGroup
              && (childAt = ((ViewGroup) mRefresherContent).getChildAt(0)) != null) {
            childAt.getLocationOnScreen(mContentLocation);
            if (mContentLocation[1] == mAbsY && (y > mLastDownY)) {
              mState = State.pulling_no_refresh;
              final OnRefreshListener onRefreshListener = mOnRefreshListener;
              if (onRefreshListener != null) {
                onRefreshListener.onStateChanged(State.pulling_no_refresh);
              }
              mHandler.sendEmptyMessageAtTime(MSG_HEADER_INVALIDATE,
                  System.currentTimeMillis() + ViewConfig.ANIMATION_FRAME_DURATION);
              return true;
            }
          } else {
            // If there's no child.
            mRefresherContent.getLocationOnScreen(mContentLocation);
            if (mContentLocation[1] == mAbsY && (y > mLastDownY)) {
              mState = State.pulling_no_refresh;
              final OnRefreshListener onRefreshListener = mOnRefreshListener;
              if (onRefreshListener != null) {
                onRefreshListener.onStateChanged(State.pulling_no_refresh);
              }
              mHandler.sendEmptyMessageAtTime(MSG_HEADER_INVALIDATE,
                  System.currentTimeMillis() + ViewConfig.ANIMATION_FRAME_DURATION);
              return true;
            }
          }
        default:
          break;
      }

      return false;
    }

    @Override
    public boolean touchEvent(MotionEvent event) {
      final int action = event.getAction() & MotionEvent.ACTION_MASK;
      final int y = (int) event.getY();

      switch (action) {
        case MotionEvent.ACTION_MOVE:
          mYOffset = Math.max(0, Math.min(y - mLastDownY, mMaxHeight * 2));

          if (mYOffset > mThresholdHeight && mState == State.pulling_no_refresh) {
            mState = State.pulling_refresh;
            final OnRefreshListener onRefreshListener = mOnRefreshListener;
            if (onRefreshListener != null) {
              onRefreshListener.onStateChanged(State.pulling_refresh);
            }
          } else if (mYOffset < mThresholdHeight && mState == State.pulling_refresh) {
            mState = State.pulling_no_refresh;

            final OnRefreshListener onRefreshListener = mOnRefreshListener;
            if (onRefreshListener != null) {
              onRefreshListener.onStateChanged(State.pulling_no_refresh);
            }
          }
          invalidate();
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
          if (mYOffset > mThresholdHeight) {
            refresh();
          } else {
            mBackPosition = 0;
          }
          animate(MSG_ANIMATE_BACK);
          break;
        default:
          break;
      }

      return true;
    }

    @Override
    public void animate(int msg) {
      mAnimator.animate(msg);
    }

    @Override
    public boolean isAnimating() {
      return mAnimator.animating;
    }
  }

  @Override
  public void setRefresherHeader(View view) {
    removeView(mRefresherHeader);
    mRefresherHeader = view;
    if (mRefresherHeader == null) {
      mEnable = false;
    } else {
      addView(mRefresherHeader);
      mEnable = mRefresherHeader != null && mRefresherContent != null;
    }
  }

  private class SideRefreshViewGroupDelegate extends Handler implements ViewGroupDelegate {
    private static final String TAG = "RefresherView$SideRefreshViewGroupInjector";

    private final int moveThreshold;

    private boolean animating;
    private long currentAnimatingTime;
    private long lastAnimationTime;
    private float animatingPosition;
    private float animationDistance;
    private int animatingVelocity;

    public SideRefreshViewGroupDelegate() {
      final float density = getResources().getDisplayMetrics().density;
      moveThreshold = (int) (ViewConfig.TOUCH_EVENT_MOVE_SLOP_SMALL * density + 0.5);
    }

    private void animateDown() {
      final long now = SystemClock.uptimeMillis();

      lastAnimationTime = now;
      currentAnimatingTime = now + ViewConfig.ANIMATION_FRAME_DURATION;
      animating = true;
      animationDistance = mThresholdHeight;
      animatingPosition = 0;
      animatingVelocity = kVelocity;

      removeMessages(MSG_ANIMATE_DOWN);
      sendEmptyMessageAtTime(MSG_ANIMATE_DOWN, currentAnimatingTime);
    }

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_ANIMATE_DOWN:
          computeDown();
          break;
        case MSG_ANIMATE_BACK:
          computeBack();
          break;
      }
    }

    private void animateBack() {
      final long now = SystemClock.uptimeMillis();

      lastAnimationTime = now;
      currentAnimatingTime = now + ViewConfig.ANIMATION_FRAME_DURATION;
      animating = true;
      animationDistance = mXOffset - mBackPosition;
      animatingPosition = 0;
      animatingVelocity = Math.max(kMinVelocity, (mXOffset - mBackPosition) * 2);

      removeMessages(MSG_ANIMATE_BACK);
      sendEmptyMessageAtTime(MSG_ANIMATE_BACK, currentAnimatingTime);
    }

    @Override
    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
      final int widthSize = widthMeasureSpec & ~(0x3 << 30);
      final int heightSize = heightMeasureSpec & ~(0x3 << 30);
      if (mRefresherContent != null) {
        measureChild(mRefresherContent, widthSize + MeasureSpec.EXACTLY,
            heightSize + MeasureSpec.EXACTLY);
      }

      if (mEmptyView != null) {
        measureChild(mEmptyView, widthSize + MeasureSpec.AT_MOST,
            heightSize + MeasureSpec.AT_MOST);
      }

      if (mRefresherHeader != null) {
        measureChild(mRefresherHeader, widthSize + MeasureSpec.AT_MOST,
            heightSize + MeasureSpec.EXACTLY);
      }

      setMeasuredDimension(widthSize, heightSize);
    }

    private void computeDown() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - lastAnimationTime) / 1000f;

      animatingPosition += animatingVelocity * t;

      if (animatingPosition >= animationDistance) {
        mXOffset = mThresholdHeight;
        animating = false;
        mState = State.idle;
        final OnRefreshListener onRefreshListener = mOnRefreshListener;
        if (onRefreshListener != null) {
          onRefreshListener.onStateChanged(State.idle);
          refresh();
        }
      } else {
        mXOffset = ViewConfig.computeInterpolator(animationDistance, animatingPosition, false);
        lastAnimationTime = now;
        currentAnimatingTime = now + ViewConfig.ANIMATION_FRAME_DURATION;
        removeMessages(MSG_ANIMATE_DOWN);
        sendEmptyMessageAtTime(MSG_ANIMATE_DOWN, currentAnimatingTime);
      }

      invalidate();
    }

    @Override
    public void layout(boolean changed, int l, int t, int r, int b) {
      final int width = r - l;
      final int height = b - t;
      if (mRefresherContent != null) {
        mRefresherContent.layout(0, 0, width, height);
      }

      if (mEmptyView != null) {
        mEmptyView.layout((width - mEmptyView.getMeasuredWidth()) / 2,
            (height - mEmptyView.getMeasuredHeight()) / 2,
            (width + mEmptyView.getMeasuredWidth()) / 2,
            (height + mEmptyView.getMeasuredHeight()) / 2);
      }

      if (mRefresherHeader != null) {
        mRefresherHeader.layout(-mRefresherHeader.getMeasuredWidth(), 0, 0, height);
      }

      getLocationOnScreen(mTempLocation);
      mAbsX = mTempLocation[0];
    }

    private void computeBack() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - lastAnimationTime) / 1000f;

      animatingPosition += animatingVelocity * t;

      if (animatingPosition >= animationDistance) {
        mXOffset = mBackPosition;
        animating = false;
        mState = State.idle;
        final OnRefreshListener onRefreshListener = mOnRefreshListener;
        if (onRefreshListener != null) {
          onRefreshListener.onStateChanged(State.idle);
        }

        if (mBackPosition == 0) {
          if (onRefreshListener != null) {
            onRefreshListener.onRefreshUI();
            mRefreshing = false;
          }
        }
      } else {
        mXOffset = (int)
            (mBackPosition + animationDistance * (1 - ViewConfig.sInterpolator.getInterpolation(
                animatingPosition / animationDistance)));
        lastAnimationTime = now;
        currentAnimatingTime = now + ViewConfig.ANIMATION_FRAME_DURATION;
        removeMessages(MSG_ANIMATE_BACK);
        sendEmptyMessageAtTime(MSG_ANIMATE_BACK, currentAnimatingTime);
      }

      invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
      final long drawingTime = getDrawingTime();

      if (mEmptyView != null) {
        drawChild(canvas, mEmptyView, drawingTime);
      }

      canvas.save();
      canvas.translate(mXOffset >> 1, 0);
      drawChild(canvas, mRefresherContent, drawingTime);
      if (mXOffset > 0) {
        drawChild(canvas, mRefresherHeader, drawingTime);
      }
      canvas.restore();
    }

    @Override
    @SuppressWarnings ("all")
    public boolean dispatchTouchEvent(MotionEvent event) {
      return RefresherView.super.dispatchTouchEvent(event) || true;
    }

    @Override
    public boolean interceptionTouchEvent(MotionEvent ev) {
      if (!mEnable || mRefreshing) {
        return false;
      }

      final int action = ev.getAction() & MotionEvent.ACTION_MASK;
      final int x = (int) ev.getX();

      switch (action) {
        case MotionEvent.ACTION_DOWN:
          mLastDownX = x;

          removeMessages(MSG_ANIMATE_BACK);
          removeMessages(MSG_ANIMATE_DOWN);
          break;

        case MotionEvent.ACTION_MOVE:
          View childAt;
          if (mRefresherContent instanceof ViewGroup
              && (childAt = ((ViewGroup) mRefresherContent).getChildAt(0)) != null) {
            childAt.getLocationOnScreen(mContentLocation);
            if (mContentLocation[0] == mAbsX && (x > mLastDownX + moveThreshold)) {
              mState = State.pulling_no_refresh;
              final OnRefreshListener onRefreshListener = mOnRefreshListener;
              if (onRefreshListener != null) {
                onRefreshListener.onStateChanged(State.pulling_no_refresh);
              }
              return true;
            }
          } else {
            // If there's no child.
            mRefresherContent.getLocationOnScreen(mContentLocation);
            if (mContentLocation[0] == mAbsX && (x > mLastDownX + moveThreshold)) {
              mState = State.pulling_no_refresh;
              final OnRefreshListener onRefreshListener = mOnRefreshListener;
              if (onRefreshListener != null) {
                onRefreshListener.onStateChanged(State.pulling_no_refresh);
              }
              return true;
            }
          }
        default:
          break;
      }

      return false;
    }

    @Override
    public boolean touchEvent(MotionEvent event) {
      final int action = event.getAction() & MotionEvent.ACTION_MASK;
      final int x = (int) event.getX();

      switch (action) {
        case MotionEvent.ACTION_MOVE:
          mXOffset = Math.max(0, Math.min(x - mLastDownX - moveThreshold, mMaxHeight * 2));

          if (mXOffset > mThresholdHeight && mState == State.pulling_no_refresh) {
            mState = State.pulling_refresh;
            final OnRefreshListener onRefreshListener = mOnRefreshListener;
            if (onRefreshListener != null) {
              onRefreshListener.onStateChanged(State.pulling_refresh);
            }
          } else if (mXOffset < mThresholdHeight && mState == State.pulling_refresh) {
            mState = State.pulling_no_refresh;

            final OnRefreshListener onRefreshListener = mOnRefreshListener;
            if (onRefreshListener != null) {
              onRefreshListener.onStateChanged(State.pulling_no_refresh);
            }
          }
          invalidate();
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
          if (mXOffset > mThresholdHeight) {
            refresh();
          } else {
            mBackPosition = 0;
          }
          animate(MSG_ANIMATE_BACK);
          break;
        default:
          break;
      }

      return true;
    }

    @Override
    public void animate(int msg) {
      switch (msg) {
        case MSG_ANIMATE_DOWN:
          animateDown();
          break;
        case MSG_ANIMATE_BACK:
          animateBack();
          break;
      }
    }

    @Override
    public boolean isAnimating() {
      return animating;
    }


  }

  @Override
  public void setEmptyView(View view) {
    removeView(mEmptyView);
    mEmptyView = view;
    if (mEmptyView != null) {
      addView(mEmptyView);
    }
  }


}
