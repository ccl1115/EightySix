package com.utree.eightysix.app.intro;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.app.account.RegisterActivity;

/**
 */
@Layout (R.layout.activity_guide)
public class GuideActivity extends BaseActivity {


  @InjectView (R.id.vp_guide)
  public ViewPager mVpGuide;
  private Page1ViewHolder mPage1ViewHolder;
  private Page2ViewHolder mPage2ViewHolder;
  private Page3ViewHolder mPage3ViewHolder;
  private View mPage1View;
  private View mPage2View;
  private View mPage3View;


  @Override
  public void onActionLeftClicked() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    WindowManager.LayoutParams attributes = getWindow().getAttributes();
    attributes.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
    }
    getWindow().setAttributes(attributes);

    mPage1View = getLayoutInflater().inflate(R.layout.page_guide_1, mVpGuide, false);
    mPage1ViewHolder = new Page1ViewHolder(mPage1View);

    mPage2View = getLayoutInflater().inflate(R.layout.page_guide_2_1, mVpGuide, false);
    mPage2ViewHolder = new Page2ViewHolder(mPage2View);

    mPage3View = getLayoutInflater().inflate(R.layout.page_guide_2, mVpGuide, false);
    mPage3ViewHolder = new Page3ViewHolder(mPage3View);

    mVpGuide.setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return 3;
      }

      @Override
      public Object instantiateItem(ViewGroup container, int position) {
        switch (position) {
          case 0: {
            container.addView(mPage1View);
            return mPage1View;
          }
          case 1: {
            container.addView(mPage2View);
            return mPage2View;
          }
          case 2: {
            container.addView(mPage3View);
            return mPage3View;
          }
        }
        return null;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
      }

      @Override
      public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
      }
    });

    final ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
      private int previous = -1;

      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override
      public void onPageSelected(int position) {
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        int currentItem = mVpGuide.getCurrentItem();
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
          switch (currentItem) {
            case 0:
              mPage2ViewHolder.hide();
              mPage3ViewHolder.hide();
              break;
            case 1:
              mPage1ViewHolder.hide();
              mPage3ViewHolder.hide();
              break;
            case 2:
              mPage1ViewHolder.hide();
              mPage2ViewHolder.hide();
              break;
          }
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
          if (currentItem != previous) {
            switch (currentItem) {
              case 0:
                mPage1ViewHolder.animate();
                break;
              case 1:
                mPage2ViewHolder.animate();
                break;
              case 2:
                mPage3ViewHolder.animate();
                break;
            }
            previous = currentItem;
          }
        }
      }
    };
    mVpGuide.setOnPageChangeListener(listener);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        listener.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
      }
    }, 1000);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected boolean shouldCheckUpgrade() {
    return false;
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onLoginEvent(Account.LoginEvent event) {
    finish();
  }

  public static class Page1ViewHolder {

    @InjectView(R.id.iv_hearts)
    public ImageView mIvHearts;

    @InjectView(R.id.iv_big_heart)
    public ImageView mIvBigHeart;

    @InjectView(R.id.iv_circle)
    public ImageView mIvCircle;

    public Page1ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void animate() {
      mIvHearts.setVisibility(View.VISIBLE);
      mIvBigHeart.setVisibility(View.VISIBLE);
      mIvCircle.setVisibility(View.VISIBLE);

      AnimatorSet circle = new AnimatorSet();

      circle.playTogether(
          ObjectAnimator.ofFloat(mIvCircle, "alpha", 0f, 1f),
          ObjectAnimator.ofFloat(mIvCircle, "scaleX", 0.3f, 1f),
          ObjectAnimator.ofFloat(mIvCircle, "scaleY", 0.6f, 1f)
      );
      circle.setDuration(500);
      circle.setInterpolator(new OvershootInterpolator(4f));

      AnimatorSet heart = new AnimatorSet();

      heart.playTogether(
          ObjectAnimator.ofFloat(mIvBigHeart, "alpha", 0f, 0f, 1f),
          ObjectAnimator.ofFloat(mIvBigHeart, "translationY", 300f, 300f, 0f)
      );
      heart.setDuration(1300);

      AnimatorSet hearts = new AnimatorSet();

      hearts.playTogether(
          ObjectAnimator.ofFloat(mIvHearts, "alpha", 0f, 1f),
          ObjectAnimator.ofFloat(mIvHearts, "translationY", -100f, 0f)
      );
      hearts.setDuration(700);

      AnimatorSet set = new AnimatorSet();
      set.playTogether(circle, hearts, heart);
      set.start();
    }

    public void hide() {
      mIvCircle.setVisibility(View.INVISIBLE);
      mIvBigHeart.setVisibility(View.INVISIBLE);
      mIvHearts.setVisibility(View.INVISIBLE);
    }
  }

  public static class Page2ViewHolder {

    @InjectView(R.id.iv_circle)
    public ImageView mIvCircle;

    @InjectView(R.id.iv_locations)
    public ImageView mIvLocations;

    @InjectView(R.id.iv_cards)
    public ImageView mIvCards;

    public Page2ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void animate() {
      mIvCards.setVisibility(View.VISIBLE);
      mIvCircle.setVisibility(View.VISIBLE);
      mIvLocations.setVisibility(View.VISIBLE);


      AnimatorSet circle = new AnimatorSet();

      circle.playTogether(
          ObjectAnimator.ofFloat(mIvCircle, "alpha", 0f, 1f),
          ObjectAnimator.ofFloat(mIvCircle, "scaleX", 0.8f, 1f),
          ObjectAnimator.ofFloat(mIvCircle, "scaleY", 0.2f, 1f)
      );
      circle.setDuration(500);
      circle.setInterpolator(new OvershootInterpolator(4f));

      AnimatorSet location = new AnimatorSet();

      location.playTogether(
          ObjectAnimator.ofFloat(mIvLocations, "alpha", 0f, 0f, 1f),
          ObjectAnimator.ofFloat(mIvLocations, "translationY", 200f, 200f, 0f)
      );
      location.setDuration(1000);

      AnimatorSet card = new AnimatorSet();

      card.playTogether(
          ObjectAnimator.ofFloat(mIvCards, "alpha", 0f, 0f, 1f),
          ObjectAnimator.ofFloat(mIvCards, "translationY", -300f, -300f, 0f)
      );
      card.setDuration(1300);

      AnimatorSet set = new AnimatorSet();
      set.playTogether(circle, location, card);
      set.start();
    }

    public void hide() {
      mIvCircle.setVisibility(View.INVISIBLE);
      mIvCards.setVisibility(View.INVISIBLE);
      mIvLocations.setVisibility(View.INVISIBLE);
    }
  }

  public static class Page3ViewHolder {

    @InjectView(R.id.iv_circle)
    public ImageView mIvCircle;

    @InjectView(R.id.iv_boy)
    public ImageView mIvBoy;

    @InjectView(R.id.iv_girl)
    public ImageView mIvGirl;

    @InjectView(R.id.ll_bottom)
    public LinearLayout mLlBottom;

    @OnClick(R.id.tv_register)
    public void onTvRegisterClicked(View view) {
      RegisterActivity.start(view.getContext(), "");
    }

    @OnClick(R.id.tv_login)
    public void onTvLoginClicked(View view) {
      LoginActivity.start(view.getContext(), "");
    }

    public Page3ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void animate() {
      mIvCircle.setVisibility(View.VISIBLE);
      mIvBoy.setVisibility(View.VISIBLE);
      mIvGirl.setVisibility(View.VISIBLE);
      mLlBottom.setVisibility(View.VISIBLE);

      AnimatorSet circle = new AnimatorSet();

      circle.playTogether(
          ObjectAnimator.ofFloat(mIvCircle, "alpha", 0f, 1f),
          ObjectAnimator.ofFloat(mIvCircle, "scaleX", 0.8f, 1f),
          ObjectAnimator.ofFloat(mIvCircle, "scaleY", 0.2f, 1f),
          ObjectAnimator.ofFloat(mLlBottom, "translationY", 300f, 0f)
      );
      circle.setDuration(200);
      circle.setInterpolator(new OvershootInterpolator(4f));

      AnimatorSet boy = new AnimatorSet();
      boy.playTogether(
          ObjectAnimator.ofFloat(mIvBoy, "alpha", 0f, 0f, 1f),
          ObjectAnimator.ofFloat(mIvBoy, "translationX", -500f, -500f, 0)
      );
      boy.setDuration(1300);
      boy.setInterpolator(new OvershootInterpolator(2f));

      AnimatorSet girl = new AnimatorSet();
      girl.playTogether(
          ObjectAnimator.ofFloat(mIvGirl, "alpha", 0f, 0f, 1f),
          ObjectAnimator.ofFloat(mIvGirl, "translationX", 500f, 500f, 0f)
      );
      girl.setDuration(1300);
      girl.setInterpolator(new OvershootInterpolator(2f));

      AnimatorSet set = new AnimatorSet();
      set.playTogether(circle, boy, girl);
      set.start();
    }

    public void hide() {
      mIvCircle.setVisibility(View.INVISIBLE);
      mIvBoy.setVisibility(View.INVISIBLE);
      mIvGirl.setVisibility(View.INVISIBLE);
      mLlBottom.setVisibility(View.INVISIBLE);
    }
  }
}