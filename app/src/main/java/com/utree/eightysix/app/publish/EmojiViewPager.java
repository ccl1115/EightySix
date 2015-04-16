/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.publish;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.widget.IndicatorView;

/**
 */
public class EmojiViewPager extends LinearLayout {

  private static final int[] PAGE1 = new int[] {
      0x1f600,
      0x1f602,
      0x1f603,
      0x1f605,
      0x1f606,
      0x1f607,
      0x1f608,
      0x1f609,
      0x1f60a,
      0x1f60b,
      0x1f60c,
      0x1f60d,
      0x1f60e,
      0x1f60f,
      0x1f611,
      0x1f613,
      0x1f614,
      0x274c,
  };

  private static final int[] PAGE2 = new int[] {
      0x1f61a,
      0x1f61b,
      0x1f61c,
      0x1f61d,
      0x1f61e,
      0x1f61f,
      0x1f621,
      0x1f622,
      0x1f623,
      0x1f624,
      0x1f625,
      0x1f628,
      0x1f62a,
      0x1f62d,
      0x1f62e,
      0x1f615,
      0x1f616,
      0x274c,
  };

  private static final int[] PAGE3 = new int[] {
      0x1f44a,
      0x1f44b,
      0x1f44c,
      0x1f44d,
      0x1f44e,
      0x1f44f,
      0x1f4a9,
      0x1f4aa,
      0x1f4a6,
      0x1f4a2,
      0x1f426,
      0x1f417,
      0x1f418,
      0x1f419,
      0x1f414,
      0x1f412,
      0x1f3ae,
      0x274c,
  };


  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView(R.id.in_panel)
  public IndicatorView mInTab;

  public EmojiViewPager(Context context) {
    this(context, null);
  }

  public EmojiViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);

    LayoutInflater.from(context).inflate(R.layout.widget_emoji_tab, this, true);

    ButterKnife.inject(this, this);

  }


  public void setFragmentManager(FragmentManager manager) {
    mVpTab.setAdapter(new FragmentPagerAdapter(manager) {
      @Override
      public Fragment getItem(int position) {
        if (position == 0) {
          return EmojiFragment.newInstance(PAGE1);
        } else if (position == 1) {
          return EmojiFragment.newInstance(PAGE2);
        } else if (position == 2) {
          return EmojiFragment.newInstance(PAGE3);
        }
        return null;
      }

      @Override
      public int getCount() {
        return 3;
      }
    });

    mVpTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
  }
}
