package com.utree.eightysix.app.feed;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.utree.eightysix.U;
import com.utree.eightysix.response.data.Post;
import com.utree.eightysix.widget.PostView;
import java.util.List;

/**
 */
class FeedAdapter extends BaseAdapter {

  private SparseBooleanArray mAnimated = new SparseBooleanArray();

  private List<Post> mPosts;

  FeedAdapter(List<Post> posts) {
    mPosts = posts;
  }

  public void add(List<Post> posts) {
    if (mPosts != null) {
      mPosts.addAll(posts);
    } else {
      mPosts = posts;
    }
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mPosts == null ? 0 : mPosts.size();
  }

  @Override
  public Post getItem(int position) {
    return mPosts.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new PostView(parent.getContext());
    }

    if (!mAnimated.get(position, false)) {
      AnimatorSet set = new AnimatorSet();
      set.playTogether(
          ObjectAnimator.ofFloat(convertView, "translationY", U.dp2px(350), 0),
          ObjectAnimator.ofFloat(convertView, "rotationX", 15, 0)
      );
      set.setDuration(600);
      set.start();
      mAnimated.put(position, true);
    } else {
      ViewHelper.setTranslationY(convertView, 0);
      ViewHelper.setRotationX(convertView, 0);
    }

    PostView postView = (PostView) convertView;
    postView.setData(getItem(position));

    return convertView;
  }

}
