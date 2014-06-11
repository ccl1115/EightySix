package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.account.GetLockpatternActivity;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.guide.GuideBuilder;
import com.utree.eightysix.widget.lockpattern.LockPatternView;
import java.util.List;

/**
 */
public class IntroActivity extends BaseActivity {

    private final PatternHelper mPatternHelper = new PatternHelper();

    @InjectView(R.id.big_logo)
    public ImageView mBigLogo;

    @InjectView(R.id.app_title)
    public TextView mAppTitle;

    @InjectView(R.id.intro_text)
    public TextView mIntroText;

    @InjectView(R.id.intro_url)
    public TextView mIntroUrl;

    @InjectView(R.id.second_layout)
    public LinearLayout mSecondLayout;

    @InjectView(R.id.lock_pattern)
    public LockPatternView mLockPatternView;

    @InjectView(R.id.tv_get_pattern)
    public TextView mGetPattern;

    @InjectView(R.id.tv_login)
    public TextView mLogin;

    private int mRetries = 0;

    @OnClick(R.id.tv_get_pattern)
    public void onTvGetPatternClick() {
        startActivity(new Intent(this, GetLockpatternActivity.class));
    }

    @OnClick(R.id.tv_login)
    public void onTvLoginClicked() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        hideTopBar(false);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Env.isPatternLocked()) {
                    animateToLockPattern();
                } else if (Env.firstRun()) {
                    startActivity(new Intent(IntroActivity.this, GuideActivity.class));
                    finish();
                } else {
                    if (Account.inst().isLogin()) {
                        //TODO Go to feeds activity or circle list activity
                    } else {
                        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            }
        }, U.getConfigInt("activity.intro.delay"));

        mLockPatternView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                if (mPatternHelper.check(pattern)) {
                    Env.setPatternLock(false);
                    if (Env.firstRun()) {
                        startActivity(new Intent(IntroActivity.this, GuideActivity.class));
                    } else {
                        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    }
                    finish();
                } else {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    showToast(R.string.wrong_pattern);
                    mRetries++;

                    if (mRetries >= mPatternHelper.getRetries()) {
                        mLockPatternView.clearPattern();
                        showGetPatternTip();
                    }
                }
            }
        });
    }

    private void animateToLockPattern() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mBigLogo, "translationY", 0, -dp2px(200)),
                ObjectAnimator.ofFloat(mBigLogo, "alpha", 1, 0),

                ObjectAnimator.ofFloat(mAppTitle, "translationY", 0, -dp2px(150)),

                ObjectAnimator.ofFloat(mIntroText, "alpha", 1, 0.2f, 0),

                ObjectAnimator.ofFloat(mIntroUrl, "translationY", 0, dp2px(70))
        );
        animatorSet.setDuration(U.getConfigInt("activity.intro.animation.duration"));
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIntroText.setVisibility(View.GONE);
                mIntroUrl.setVisibility(View.GONE);
                mSecondLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void showGetPatternTip() {
        new GuideBuilder()
                .setTargetView(mGetPattern)
                .setAlpha(140)
                .setEnterAnimationId(android.R.anim.fade_in)
                .setExitAnimationId(android.R.anim.fade_out)
                .setAutoDismiss(true)
                .createGuide()
                .show(this);
    }
}
