package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;

/**
 * @author simon
 */
public class RefreshIndicator extends FrameLayout {

  @InjectView(R.id.tv_loading)
  TextView mTvText;

  @InjectView(R.id.ri_progress_bar)
  ProgressBar mProgress;

  public RefreshIndicator(Context context) {
    this(context, null);
  }

  public RefreshIndicator(Context context, AttributeSet attrs) {
    super(context, attrs, R.attr.refreshIndicatorStyle);

    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.widget_refresh_indicator, this, true);
    ButterKnife.inject(this, this);
    setClickable(true);
  }

  public void setText(String text) {
    mTvText.setText(text);
  }

  public void show() {
    if (getVisibility() == VISIBLE) return;

    ObjectAnimator tag = (ObjectAnimator) getTag();
    if (tag != null) tag.cancel();

    setVisibility(VISIBLE);
    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationY", -getMeasuredHeight(), 0);
    animator.setDuration(200);
    animator.start();
    setTag(animator);
  }

  public void show(boolean progressing) {
    if (progressing) {
      mProgress.setVisibility(VISIBLE);
      mTvText.setText("刷新中...");
    } else {
      mProgress.setVisibility(GONE);
      mTvText.setText("下拉刷新");
    }

    show();
  }

  public void hide() {
    if (getVisibility() == INVISIBLE) return;

    ObjectAnimator tag = (ObjectAnimator) getTag();
    if (tag != null) tag.cancel();
    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationY", 0, -getMeasuredHeight());
    animator.setDuration(200);
    animator.start();
    animator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        setVisibility(INVISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    setTag(animator);
  }

}
