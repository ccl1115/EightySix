package com.utree.eightysix.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;

/**
 * @author simon
 */
public class TitleTab extends FrameLayout {

  private ViewPager mViewPager;

  @InjectView(R.id.ll_tabs)
  public LinearLayout mLlTabs;

  @InjectView(R.id.in_tab)
  public IndicatorView mInTab;
  private ViewPager.OnPageChangeListener mListener;
  private OnTabItemClickedListener mOnTabItemClickedListener;

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

    setBackgroundColor(0xffebe9f0);
  }

  public void setViewPager(ViewPager viewPager) {
    mViewPager = viewPager;

    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mInTab.setPosition(position + positionOffset);
        if (mListener != null) {
          mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
      }

      @Override
      public void onPageSelected(int position) {
        if (mListener != null) {
          mListener.onPageSelected(position);
        }

        for (int i = 0, size = mLlTabs.getChildCount(); i < size; i++) {
          TextView textView = (TextView) mLlTabs.getChildAt(i).findViewById(R.id.tv_title);
          textView.setTextColor(getResources().getColor(R.color.apptheme_primary_grey_color_pressed));
          textView.setTextSize(14);
        }

        TextView textView = (TextView) mLlTabs.getChildAt(position).findViewById(R.id.tv_title);
        textView.setTextColor(getResources().getColor(R.color.apptheme_primary_light_color));
        textView.setTextSize(16);
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
          mListener.onPageScrollStateChanged(state);
        }
      }
    });

    PagerAdapter adapter = mViewPager.getAdapter();
    if (adapter != null) {
      mInTab.setCount(adapter.getCount());
      for (int i = 0, size = adapter.getCount(); i < size; i++) {
        mLlTabs.addView(buildTab(adapter.getPageTitle(i), i));
      }
    }
  }

  public void setOnPageChangedListener(ViewPager.OnPageChangeListener listener) {
    mListener = listener;
  }

  private View buildTab(CharSequence name, final int i) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_title_tab_item, mLlTabs, false);

    TextView textView = (TextView) view.findViewById(R.id.tv_title);
    textView.setText(name);

    if (i == 0) {
      textView.setTextColor(getResources().getColor(R.color.apptheme_primary_light_color));
      textView.setTextSize(16);
    }

    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnTabItemClickedListener != null) {
          mOnTabItemClickedListener.onTabItemClicked(v, i);
        }
        mViewPager.setCurrentItem(i, true);
      }
    });
    return view;
  }

  public void setTabBudget(int position, String text, boolean hide) {
    RoundedButton roundedButton = (RoundedButton) mLlTabs.getChildAt(position).findViewById(R.id.rb_budget);
    if (hide) {
      roundedButton.setVisibility(INVISIBLE);
    } else {
      roundedButton.setVisibility(VISIBLE);
    }
    roundedButton.setText(text);
  }

  public boolean hasBudget(int position) {
    RoundedButton roundedButton = (RoundedButton) mLlTabs.getChildAt(position).findViewById(R.id.rb_budget);
    return roundedButton.getVisibility() == VISIBLE;
  }

  public void setTabText(int position, String text) {
    ((TextView) mLlTabs.getChildAt(position).findViewById(R.id.tv_title)).setText(text);
  }

  public void setOnTabItemClicked(OnTabItemClickedListener listener) {
    mOnTabItemClickedListener = listener;
  }

  public interface OnTabItemClickedListener {
    public void onTabItemClicked(View view, int position);
  }
}
