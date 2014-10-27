package com.utree.eightysix.app.publish;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 */
public class PublishLayout extends ViewGroup {

  public final static int PANEL_INFO = 0x0;
  private int mLastPanel = PANEL_INFO;
  public final static int PANEL_COLOR = 0x1;
  public final static int PANEL_TAGS = 0x2;
  @InjectView (R.id.fl_top)
  public FrameLayout mFlTop;

  @InjectView (R.id.rl_panel)
  public RelativeLayout mRlPanel;

  @InjectView (R.id.ll_bottom)
  public LinearLayout mLlInfo;

  @InjectView (R.id.fl_grid_panel)
  public FrameLayout mFlGridPanel;

  @InjectView (R.id.fl_tags)
  public FrameLayout mFlTags;

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

  /**
   * @param panel the panel id
   * @see #PANEL_COLOR
   * @see #PANEL_INFO
   * @see #PANEL_TAGS
   */
  public void switchToPanel(int panel) {
    mLastPanel = panel;
    if (mLastPanel == PANEL_COLOR) {
      mFlGridPanel.setVisibility(VISIBLE);
      mLlInfo.setVisibility(GONE);
      mFlTags.setVisibility(GONE);
    } else if (mLastPanel == PANEL_INFO) {
      mLlInfo.setVisibility(VISIBLE);
      mFlGridPanel.setVisibility(GONE);
      mFlTags.setVisibility(GONE);
    } else if (mLastPanel == PANEL_TAGS) {
      mFlTags.setVisibility(VISIBLE);
      mFlGridPanel.setVisibility(GONE);
      mLlInfo.setVisibility(GONE);
    }
    mPanelHidden = false;
    requestLayout();
    invalidate();
  }

  public void hidePanel() {
    mFlGridPanel.setVisibility(GONE);
    mLlInfo.setVisibility(GONE);
    mFlTags.setVisibility(GONE);
    mPanelHidden = true;
  }

  public void showPanel() {
    if (mLastPanel == PANEL_COLOR) {
      mFlGridPanel.setVisibility(VISIBLE);
      mLlInfo.setVisibility(GONE);
      mFlTags.setVisibility(GONE);
    } else if (mLastPanel == PANEL_INFO) {
      mLlInfo.setVisibility(VISIBLE);
      mFlGridPanel.setVisibility(GONE);
      mFlTags.setVisibility(GONE);
    } else if (mLastPanel == PANEL_TAGS) {
      mLlInfo.setVisibility(GONE);
      mFlGridPanel.setVisibility(GONE);
      mFlTags.setVisibility(VISIBLE);
    }

    mPanelHidden = false;
  }

  public int getCurrentPanel() {
    return mLastPanel;
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    mFlTop.layout(l, 0, r, mFlTop.getMeasuredHeight());
    mRlPanel.layout(l, mFlTop.getBottom(), r, mFlTop.getBottom() + mRlPanel.getMeasuredHeight());
    if (mPanelHidden) {
      mFlGridPanel.layout(0, 0, 0, 0);
    } else if (mLastPanel == PANEL_INFO) {
      mLlInfo.layout(l, mRlPanel.getBottom(), r, mRlPanel.getBottom() + mLlInfo.getMeasuredHeight());
      mFlGridPanel.layout(0, 0, 0, 0);
      mFlTags.layout(0, 0, 0, 0);
    } else if (mLastPanel == PANEL_COLOR) {
      mFlGridPanel.layout(l, mRlPanel.getBottom(), r, mRlPanel.getBottom() + mFlGridPanel.getMeasuredHeight());
      mLlInfo.layout(0, 0, 0, 0);
      mFlTags.layout(0, 0, 0, 0);
    } else if (mLastPanel == PANEL_TAGS) {
      mFlTags.layout(l, mRlPanel.getBottom(), r, mRlPanel.getBottom() + mFlTags.getMeasuredHeight());
      mFlGridPanel.layout(0, 0, 0, 0);
      mLlInfo.layout(0, 0, 0, 0);
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

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    int heightLeft = heightSize;

    if (mLastPanel == PANEL_INFO && !mPanelHidden) {
      measureChild(mLlInfo, widthMeasureSpec, heightSize + MeasureSpec.AT_MOST);
      heightLeft -= mLlInfo.getMeasuredHeight();
    }

    if (mLastPanel == PANEL_COLOR && !mPanelHidden) {
      measureChild(mFlGridPanel, widthMeasureSpec, heightSize + MeasureSpec.AT_MOST);
      heightLeft -= mFlGridPanel.getMeasuredHeight();
    }

    if (mLastPanel == PANEL_TAGS && !mPanelHidden) {
      measureChild(mFlTags, widthMeasureSpec, heightSize + MeasureSpec.AT_MOST);
      heightLeft -= mFlTags.getMeasuredHeight();
    }

    measureChild(mRlPanel, widthMeasureSpec, heightLeft + MeasureSpec.AT_MOST);
    heightLeft -= mRlPanel.getMeasuredHeight();
    measureChild(mFlTop, widthMeasureSpec, heightLeft + MeasureSpec.EXACTLY);

    setMeasuredDimension(widthSize, heightSize);
  }
}
