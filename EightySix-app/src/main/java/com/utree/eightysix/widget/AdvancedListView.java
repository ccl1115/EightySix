package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;

/**
 * @author simon
 */
public final class AdvancedListView extends ListView {

  private LoadMoreAdapterWrapper mAdapterWrapper;

  private LoadMoreCallback mLoadMoreCallback;

  private OnTopOverScrollListener mOnTopOverScrollListener;

  private boolean mOverScrolled;
  private float mLastDownY;

  public AdvancedListView(Context context) {
    this(context, null, 0);
  }

  public AdvancedListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AdvancedListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setCacheColorHint(0x00000000);
    setSelector(getResources().getDrawable(R.drawable.transparent));
  }

  @Override
  public void setAdapter(ListAdapter adapter) {
    if (adapter != null) {
      mAdapterWrapper = new LoadMoreAdapterWrapper(adapter);
      if (mLoadMoreCallback != null) {
        mAdapterWrapper.setLoadMoreCallback(mLoadMoreCallback);
      }
      super.setAdapter(mAdapterWrapper);
    } else {
      super.setAdapter(null);
    }
  }

  public void setLoadMoreCallback(LoadMoreCallback callback) {
    mLoadMoreCallback = callback;
    if (mAdapterWrapper != null) {
      mAdapterWrapper.setLoadMoreCallback(callback);
      super.setAdapter(mAdapterWrapper);
    }
  }

  public void stopLoadMore() {
    if (mAdapterWrapper != null) {
      mAdapterWrapper.stopLoadMore();
    }
  }

  public void setOnTopOverScrollListener(OnTopOverScrollListener listener) {
    mOnTopOverScrollListener = listener;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (mOnTopOverScrollListener != null && getFirstVisiblePosition() == 0) {
      View view = getChildAt(0);
      if (view != null && view.getTop() >= 0) {
        final float y = ev.getY();
        final int action = ev.getActionMasked();

        switch (action) {
          case MotionEvent.ACTION_DOWN:
            mLastDownY = y;
            break;
          case MotionEvent.ACTION_MOVE:
            if (!mOverScrolled && (y - mLastDownY) > ViewConfig.TOUCH_EVENT_MOVE_SLOP_MEDIUM) {
              mOverScrolled = true;
              mOnTopOverScrollListener.onStateChanged(OnTopOverScrollListener.OVER_SCROLLING);
              mLastDownY = ev.getY();
            } else if (mOverScrolled) {
              mOnTopOverScrollListener.onOverScroll((int) (ev.getY() - mLastDownY));
            }

            break;
          case MotionEvent.ACTION_CANCEL:
          case MotionEvent.ACTION_UP:
            if (mOverScrolled) {
              mOverScrolled = false;
              mOnTopOverScrollListener.onStateChanged(OnTopOverScrollListener.IDLE);
            }
            break;
        }
      } else if (mOverScrolled) {
        mOverScrolled = false;
        mOnTopOverScrollListener.onStateChanged(OnTopOverScrollListener.IDLE);
      }
    } else if (mOverScrolled) {
      mOverScrolled = false;
    }
    return super.dispatchTouchEvent(ev);
  }

  public interface OnTopOverScrollListener {

    int OVER_SCROLLING = 1;
    int IDLE = 2;

    void onOverScroll(int distance);

    void onStateChanged(int state);
  }

  @Subscribe public void onAdapterDataSetChangedEvent(AdapterDataSetChangedEvent event) {
    if (mAdapterWrapper != null) {
      mAdapterWrapper.notifyDataSetChanged();
    }
  }
}
