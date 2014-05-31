package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;

/**
 */
@Layout(R.layout.activity_feed)
public class FeedActivity extends BaseActivity {

    @ViewId(R.id.lv_feed)
    public ListView mLvFeed;

    @ViewId(R.id.ib_send)
    @OnClick
    public ImageButton mSend;

    @ViewId(R.id.ib_refresh)
    @OnClick
    public ImageButton mRefresh;

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.ib_refresh:
                break;
            case R.id.ib_send:
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLvFeed.setAdapter(new FeedAdapter());
            }
        }, 2000);

        mLvFeed.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int mPreFirstVisibleItem;

            private AnimatorSet mDownSet = new AnimatorSet();
            private AnimatorSet mUpSet = new AnimatorSet();

            private boolean mIsDown = false;
            private boolean mIsUp = true;

            {
                mDownSet.setDuration(500);
                mDownSet.playTogether(
                        ObjectAnimator.ofFloat(mSend, "translationY", 0f, 200f),
                        ObjectAnimator.ofFloat(mRefresh, "translationY", 0f, 200f)
                );
                mDownSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIsDown = true;
                        mIsUp = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                mUpSet.setDuration(500);
                mUpSet.playTogether(
                        ObjectAnimator.ofFloat(mSend, "translationY", 200f, 0f),
                        ObjectAnimator.ofFloat(mRefresh, "translationY", 200f, 0f)
                );
                mUpSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIsUp = true;
                        mIsDown = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > mPreFirstVisibleItem) {
                    if (!mDownSet.isRunning() && !mIsDown) {
                        mDownSet.start();
                    }
                } else if (firstVisibleItem < mPreFirstVisibleItem) {
                    if (!mUpSet.isRunning() && !mIsUp) {
                        mUpSet.start();
                    }
                }
                mPreFirstVisibleItem = firstVisibleItem;
            }
        });

    }
}