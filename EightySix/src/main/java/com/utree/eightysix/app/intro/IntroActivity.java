package com.utree.eightysix.app.intro;

import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        hideTopBar(false);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mBigLogo, "translationY", 0, -400),
                ObjectAnimator.ofFloat(mBigLogo, "alpha", 1, 0),

                ObjectAnimator.ofFloat(mAppTitle, "translationY", 0, -400),

                ObjectAnimator.ofFloat(mIntroText, "translationY", 0, -400),
                ObjectAnimator.ofFloat(mIntroText, "alpha", 1, 0)
        );
        animatorSet.setDuration(2000);
        animatorSet.setStartDelay(2000);
        animatorSet.start();
    }

}
