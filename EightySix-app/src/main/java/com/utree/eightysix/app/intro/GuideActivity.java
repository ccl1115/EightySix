package com.utree.eightysix.app.intro;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import butterknife.InjectView;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

/**
 */
@Layout (R.layout.activity_guide)
public class GuideActivity extends BaseActivity {

  private static final int PAGE_1_BACKGROUND_COLOR = 0xff43cf76;
  private static final int PAGE_2_BACKGROUND_COLOR = 0xff3f61a9;
  private static final int PAGE_3_BACKGROUND_COLOR = 0xff55b5c3;

  private ValueAnimator mPageColorAnimator =
      ValueAnimator.ofObject(new ArgbEvaluator(), PAGE_1_BACKGROUND_COLOR, PAGE_2_BACKGROUND_COLOR, PAGE_3_BACKGROUND_COLOR);

  {
    mPageColorAnimator.setDuration(2000);
    mPageColorAnimator.setInterpolator(new LinearInterpolator());
  }

  @InjectView (R.id.vp_guide)
  public ViewPager mVpGuide;

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    WindowManager.LayoutParams attributes = getWindow().getAttributes();
    attributes.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
    getWindow().setAttributes(attributes);

    mVpGuide.setAdapter(new PagerAdapter() {
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
        LayoutInflater inflater = LayoutInflater.from(GuideActivity.this);
        switch (position) {
          case 0: {
            View inflate = inflater.inflate(R.layout.page_guide_1, container, false);
            container.addView(inflate);
            return inflate;
          }
          case 1: {
            View inflate = inflater.inflate(R.layout.page_guide_2, container, false);
            container.addView(inflate);
            return inflate;
          }
          case 2: {
            View inflate = inflater.inflate(R.layout.page_guide_3, container, false);
            container.addView(inflate);
            return inflate;
          }
        }
        return null;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
      }
    });

    mVpGuide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPageColorAnimator.setCurrentPlayTime((long) (1000 * (position + positionOffset)));
        mVpGuide.setBackgroundColor((Integer) mPageColorAnimator.getAnimatedValue());
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}