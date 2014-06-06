package com.utree.eightysix.app.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.account.GetLockpatternActivity;
import com.utree.eightysix.app.account.LoginActivity;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import com.utree.eightysix.widget.guide.GuideBuilder;
import com.utree.eightysix.widget.lockpattern.LockPatternView;
import java.util.List;

/**
 */
public class IntroActivity extends BaseActivity {

    private final PatternHelper mPatternHelper = new PatternHelper();

    @ViewId(R.id.big_logo)
    public ImageView mBigLogo;

    @ViewId(R.id.app_title)
    public TextView mAppTitle;

    @ViewId(R.id.intro_text)
    public TextView mIntroText;

    @ViewId(R.id.intro_url)
    public TextView mIntroUrl;

    @ViewId(R.id.second_layout)
    public LinearLayout mSecondLayout;

    @ViewId(R.id.lock_pattern)
    public LockPatternView mLockPatternView;

    @ViewId(R.id.tv_get_pattern)
    @OnClick
    public TextView mGetPattern;

    @ViewId(R.id.tv_login)
    @OnClick
    public TextView mLogin;

    private int mRetries = 0;

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.tv_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.tv_get_pattern:
                startActivity(new Intent(this, GetLockpatternActivity.class));
                break;
            default:
                break;
        }
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
                } else {
                    if (Account.inst().isLogin()) {

                    } else {
                        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    }
                }
                finish();
            }
        }, 1500);

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
        animatorSet.setDuration(1500);
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
