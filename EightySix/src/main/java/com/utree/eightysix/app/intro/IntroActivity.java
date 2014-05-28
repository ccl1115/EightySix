package com.utree.eightysix.app.intro;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.utils.ViewBinding;

/**
 */
public class IntroActivity extends BaseActivity {

    @ViewBinding.ViewId(R.id.big_logo)
    public ImageView mBigLogo;

    @ViewBinding.ViewId(R.id.app_title)
    public TextView mAppTitle;

    @ViewBinding.ViewId(R.id.intro_text)
    public TextView mIntroText;

    @ViewBinding.ViewId(R.id.intro_url)
    public TextView mIntroUrl;

    @ViewBinding.ViewId(R.id.second_layout)
    public LinearLayout mSecondLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        hideTopBar(false);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mBigLogo, "translationY", 0, -dp2px(200)),
                ObjectAnimator.ofFloat(mBigLogo, "alpha", 1, 0),

                ObjectAnimator.ofFloat(mAppTitle, "translationY", 0, -dp2px(130)),

                ObjectAnimator.ofFloat(mIntroText, "alpha", 1, 0.2f, 0),

                ObjectAnimator.ofFloat(mIntroUrl, "translationY", 0, dp2px(70))
        );
        animatorSet.setDuration(1500);
        animatorSet.setStartDelay(1500);
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

}
