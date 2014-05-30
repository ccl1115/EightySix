package com.utree.eightysix.app.feed;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 */
class FeedAdapter extends BaseAdapter {

    private SparseBooleanArray mAnimated = new SparseBooleanArray(100);

    FeedAdapter() {
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_post, null);
        }

        if (!mAnimated.get(position, false)) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(convertView, "alpha", 0.7f, 1f),
                    ObjectAnimator.ofFloat(convertView, "translationY", U.dp2px(350), 0),
                    ObjectAnimator.ofFloat(convertView, "rotationX", 20, 0)
            );
            set.setDuration(600);
            set.start();
        }
        mAnimated.put(position, true);

        return convertView;
    }

}
