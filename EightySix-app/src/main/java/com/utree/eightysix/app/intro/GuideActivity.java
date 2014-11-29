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
import com.utree.eightysix.app.post.PostPostView;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.IndicatorView;

import java.util.Random;

/**
 */
@Layout (R.layout.activity_guide)
public class GuideActivity extends BaseActivity {

  private static final Post LOCAL_POST = new Post();

  static {
    LOCAL_POST.source = "仁宝电脑";
    LOCAL_POST.comments = 165;
    LOCAL_POST.praise = 391;
    LOCAL_POST.content = "找我50块假钱的傻逼，每天都在宿舍门口碰到！好想上去揍他。。。";
  }


  private static final int PAGE_1_BACKGROUND_COLOR = 0xff699de6;
  private static final int PAGE_2_BACKGROUND_COLOR = 0xffff8474;
  private static final int PAGE_2_1_BACKGROUND_COLOR = 0xff864bfd;
  private static final int PAGE_3_BACKGROUND_COLOR = 0xff55b5c3;
  private ValueAnimator mPageColorAnimator =
      ValueAnimator.ofObject(new ArgbEvaluator(), PAGE_1_BACKGROUND_COLOR,
          PAGE_2_BACKGROUND_COLOR,
          PAGE_2_1_BACKGROUND_COLOR,
          PAGE_3_BACKGROUND_COLOR);

  private static final int[] RANDOM_BACKGROUND = {
      R.drawable.guide_post_bg,
  };

  @InjectView (R.id.vp_guide)
  public ViewPager mVpGuide;
  @InjectView (R.id.iv_bottom_wave)
  public ImageView mIvWave;
  @InjectView (R.id.in_guide)
  public IndicatorView mInGuide;
  private int mRandomBg;

  {
    mPageColorAnimator.setDuration(3000);
    mPageColorAnimator.setInterpolator(new LinearInterpolator());
    mRandomBg = RANDOM_BACKGROUND[new Random().nextInt(RANDOM_BACKGROUND.length)];
  }

  private Page3ViewHolder mPage3ViewHolder;
  private Post mPost;

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
        return 4;
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
            View inflate = inflater.inflate(R.layout.page_guide_2_1, container, false);
            container.addView(inflate);
            return inflate;
          }
          case 2: {
            View inflate = inflater.inflate(R.layout.page_guide_2, container, false);
            container.addView(inflate);
            return inflate;
          }
          case 3: {
            View inflate = inflater.inflate(R.layout.page_guide_3, container, false);
            mPage3ViewHolder = new Page3ViewHolder(inflate);
            container.addView(inflate);
            mPage3ViewHolder.mPostPostView.mTvPraise.setOnClickListener(null);
            mPage3ViewHolder.mPostPostView.setData(LOCAL_POST);
            mPage3ViewHolder.mPostPostView.mAivBg.setImageResource(mRandomBg);
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
        mInGuide.setPosition(position + positionOffset);

        if (position == 2 && positionOffset > 0f) {
          ViewHelper.setTranslationY(mIvWave, 40 * positionOffset);
          ViewHelper.setAlpha(mPage3ViewHolder.mPostPostView, positionOffset);
          ViewHelper.setAlpha(mInGuide, 1 - positionOffset);
        } else if (position == 3 && positionOffset < 0f) {
          ViewHelper.setTranslationY(mIvWave, 40 + (40 * positionOffset));
          ViewHelper.setAlpha(mPage3ViewHolder.mPostPostView, 1 + positionOffset);
          ViewHelper.setAlpha(mInGuide, -positionOffset);
        }
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

  public class Page3ViewHolder {
    @InjectView (R.id.post_post_view)
    public PostPostView mPostPostView;

    public Page3ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.tv_login)
    public void onTvLoginClicked() {
      startActivity(new Intent(GuideActivity.this, LoginActivity.class));
    }

    @OnClick (R.id.tv_join)
    public void onTvJoinClicked() {
      startActivity(new Intent(GuideActivity.this, RegisterActivity.class));
    }

    @OnClick (R.id.tv_forget_pwd)
    public void onTvForgetPwdClicked() {
      startActivity(new Intent(GuideActivity.this, ForgetPwdActivity.class));
    }
  }
}