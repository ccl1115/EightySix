package com.utree.eightysix.app.publish;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 */
public class PublishLayout extends ViewGroup {

  public final static int PANEL_INFO = 0x0;
  public final static int PANEL_COLOR = 0x1;

  private int mLastPanel = PANEL_INFO;

  @InjectView (R.id.fl_top)
  public FrameLayout mFlTop;

  @InjectView (R.id.fl_panel)
  public FrameLayout mFlPanel;

  @InjectView (R.id.ll_bottom)
  public LinearLayout mLlBottom;

  @InjectView (R.id.fl_grid_panel)
  public FrameLayout mFlGridPanel;

  private boolean mPanelHidden = false;

  public PublishLayout(Context context) {
    this(context, null, 0);
  }

  public PublishLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PublishLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    inflate(context, R.layout.activity_publish, this);
    U.viewBinding(this, this);

  }

  public void switchPanel() {
    if (mLastPanel == PANEL_COLOR) {
      mLastPanel = PANEL_INFO;
      mLlBottom.setVisibility(VISIBLE);
      mFlGridPanel.setVisibility(GONE);
    } else {
      mLastPanel = PANEL_COLOR;
      mFlGridPanel.setVisibility(VISIBLE);
      mLlBottom.setVisibility(GONE);
    }
    mPanelHidden = false;
    requestLayout();
    invalidate();
  }

  /**
   * @param panel the panel id
   * @see #PANEL_COLOR
   * @see #PANEL_INFO
   */
  public void switchToPanel(int panel) {
    mLastPanel = panel;
    if (mLastPanel == PANEL_COLOR) {
      mFlGridPanel.setVisibility(VISIBLE);
      mLlBottom.setVisibility(GONE);
    } else {
      mLlBottom.setVisibility(VISIBLE);
      mFlGridPanel.setVisibility(GONE);
    }
    mPanelHidden = false;
    requestLayout();
    invalidate();
  }

  public void hidePanel() {
    mFlGridPanel.setVisibility(GONE);
    mLlBottom.setVisibility(GONE);
    mPanelHidden = true;
  }

  public void showPanel() {
    if (mLastPanel == PANEL_COLOR) {
      mFlGridPanel.setVisibility(VISIBLE);
      mLlBottom.setVisibility(GONE);
    } else if (mLastPanel == PANEL_INFO) {
      mLlBottom.setVisibility(VISIBLE);
      mFlGridPanel.setVisibility(GONE);
    }

    mPanelHidden = false;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    int heightLeft = heightSize;

    if (mLastPanel == PANEL_INFO && !mPanelHidden) {
      measureChild(mLlBottom, widthMeasureSpec, heightSize + MeasureSpec.AT_MOST);
      heightLeft -= mLlBottom.getMeasuredHeight();
    }

    if (mLastPanel == PANEL_COLOR && !mPanelHidden) {
      measureChild(mFlGridPanel, widthMeasureSpec, heightSize + MeasureSpec.AT_MOST);
      heightLeft -= mFlGridPanel.getMeasuredHeight();
    }

    measureChild(mFlPanel, widthMeasureSpec, heightLeft + MeasureSpec.AT_MOST);
    heightLeft -= mFlPanel.getMeasuredHeight();
    measureChild(mFlTop, widthMeasureSpec, heightLeft + MeasureSpec.EXACTLY);

    setMeasuredDimension(widthSize, heightSize);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    mFlTop.layout(l, 0, r, mFlTop.getMeasuredHeight());
    mFlPanel.layout(l, mFlTop.getBottom(), r, mFlTop.getBottom() + mFlPanel.getMeasuredHeight());
    if (mPanelHidden) {
      mFlGridPanel.layout(0, 0, 0, 0);
    } else if (mLastPanel == PANEL_INFO) {
      mLlBottom.layout(l, mFlPanel.getBottom(), r, mFlPanel.getBottom() + mLlBottom.getMeasuredHeight());
      mFlGridPanel.layout(0, 0, 0, 0);
    } else if (mLastPanel == PANEL_COLOR) {
      mFlGridPanel.layout(l, mFlPanel.getBottom(), r, mFlPanel.getBottom() + mFlGridPanel.getMeasuredHeight());
      mLlBottom.layout(0, 0, 0, 0);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      if (h > oldh) {
        post(new Runnable() {
          @Override
          public void run() {
            showPanel();
          }
        });
      } else {
        post(new Runnable() {
          @Override
          public void run() {
            hidePanel();
          }
        });
      }
    }
  }
}
