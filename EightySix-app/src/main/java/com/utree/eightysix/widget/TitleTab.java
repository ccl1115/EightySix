package com.utree.eightysix.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class TitleTab extends FrameLayout {

  private ViewPager mViewPager;

  @InjectView(R.id.ll_tabs)
  public LinearLayout mLlTabs;

  @InjectView(R.id.in_tab)
  public IndicatorView mInTab;

  public TitleTab(Context context) {
    this(context, null, 0);
  }

  public TitleTab(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TitleTab(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    LayoutInflater.from(context).inflate(R.layout.widget_title_tab, this, true);

    ButterKnife.inject(this);
  }

  public void setViewPager(ViewPager viewPager) {
    mViewPager = viewPager;

    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mInTab.setPosition(position + positionOffset);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    PagerAdapter adapter = mViewPager.getAdapter();
    if (adapter != null) {
      mInTab.setCount(adapter.getCount());
      for (int i = 0, size = adapter.getCount(); i < size; i++) {
        mLlTabs.addView(buildTab(adapter.getPageTitle(i)));
      }
    }
  }

  private View buildTab(CharSequence name) {
    TextView tv = new TextView(getContext());
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);

    lp.leftMargin = U.dp2px(8);
    lp.topMargin = U.dp2px(8);
    lp.rightMargin = U.dp2px(8);
    lp.bottomMargin = U.dp2px(8);

    lp.weight = 1;
    tv.setLayoutParams(lp);
    tv.setText(name);
    tv.setGravity(Gravity.CENTER);
    return tv;
  }
}
