package com.utree.eightysix.app.feed;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.widget.IndicatorView;

/**
 * @author simon
 */
public class RewardFragment extends BaseFragment {

  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    FragmentManager manager = getFragmentManager();
    if (manager != null) {
      manager.beginTransaction()
          .detach(this)
          .commit();
    }
  }

  @InjectView(R.id.ll_frame)
  public LinearLayout mLlFrame;

  @InjectView(R.id.vp_page)
  public ViewPager mVpPage;

  @InjectView(R.id.in_page)
  public IndicatorView mInPage;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_reward, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mLlFrame.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(8), Color.WHITE));

    mVpPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mInPage.setPosition(position + positionOffset);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    mVpPage.setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return 3;
      }

      @Override
      public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
      }

      @Override
      public Object instantiateItem(ViewGroup container, int position) {
        switch (position) {
          case 0: {
            View view = LayoutInflater.from(RewardFragment.this.getActivity())
                .inflate(R.layout.page_reward_1, container, false);
            container.addView(view);
            return view;
          }
          case 1: {
            View view = LayoutInflater.from(RewardFragment.this.getActivity())
                .inflate(R.layout.page_reward_2, container, false);
            container.addView(view);
            return view;
          }
          case 2: {
            View view = LayoutInflater.from(RewardFragment.this.getActivity())
                .inflate(R.layout.page_reward_3, container, false);
            container.addView(view);
            return view;
          }
        }
        return null;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
      }
    });
  }
}