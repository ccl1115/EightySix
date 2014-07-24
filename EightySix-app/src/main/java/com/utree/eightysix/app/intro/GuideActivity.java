package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ForgetPwdActivity;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.app.account.RegisterActivity;
import com.utree.eightysix.app.feed.PostPostView;
import com.utree.eightysix.request.RegHotRequest;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;

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

  @InjectView (R.id.iv_bottom_wave)
  public ImageView mIvWave;

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
            new Page3ViewHolder(inflate);
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

      @Override
      public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
      }
    });

    mVpGuide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPageColorAnimator.setCurrentPlayTime((long) (1000 * (position + positionOffset)));
        mVpGuide.setBackgroundColor((Integer) mPageColorAnimator.getAnimatedValue());

        if (position == 1 && positionOffset > 0f) {
          ViewHelper.setTranslationY(mIvWave, 40 * positionOffset);
        } else if (position == 2 && positionOffset < 0f) {
          ViewHelper.setTranslationY(mIvWave, 40 + (40 * positionOffset));
        }
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        requestRegPost();
      }
    }, 500);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Env.setFirstRun(false);
  }

  private void requestRegPost() {
    request(new RegHotRequest(), new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {

      }
    }, Response.class);
  }

  public class Page3ViewHolder {
    @OnClick (R.id.tv_login)
    public void onTvLoginClicked() {
      startActivity(new Intent(GuideActivity.this, LoginActivity.class));
    }

    @OnClick (R.id.rb_join)
    public void onTvJoinClicked() {
      startActivity(new Intent(GuideActivity.this, RegisterActivity.class));
    }

    @OnClick (R.id.tv_forget_pwd)
    public void onTvForgetPwdClicked() {
      startActivity(new Intent(GuideActivity.this, ForgetPwdActivity.class));
    }

    @InjectView(R.id.post_post_view)
    public PostPostView mPostPostView;

    public Page3ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}