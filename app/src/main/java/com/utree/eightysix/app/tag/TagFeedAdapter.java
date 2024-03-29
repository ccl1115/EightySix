package com.utree.eightysix.app.tag;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedPostView;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.TagFeeds;

import java.util.List;

/**
 */
class TagFeedAdapter extends BaseAdapter {

  public static final int TYPE_COUNT = 3;
  static final int TYPE_PLACEHOLDER = 0;
  static final int TYPE_POST = 1;

  public static final int FEED_HOT = 1;
  public static final int FEED_FACTORY =2;

  private int mFeedType;

  private SparseBooleanArray mAnimated = new SparseBooleanArray();

  private TagFeeds mFeeds;

  public TagFeedAdapter(TagFeeds feeds, int type) {
    mFeeds = feeds;
    mFeedType = type;
  }

  public void add(List<BaseItem> posts) {
    if (mFeeds.posts.lists == null) {
      mFeeds.posts.lists = posts;
    } else {
      int size = mFeeds.posts.lists.size();
      mFeeds.posts.lists.addAll(posts);
      if (size == 1) {
        notifyDataSetInvalidated();
      } else {
        notifyDataSetChanged();
      }
    }
  }

  public void add(BaseItem post) {
    List<BaseItem> lists = mFeeds.posts.lists;
    for (int i = 0, listsSize = lists.size(); i < listsSize; i++) {
      BaseItem item = lists.get(i);
      if (item instanceof Post) {
        mFeeds.posts.lists.add(i, post);
        notifyDataSetChanged();
        return;
      }
    }
    lists.add(post);
    notifyDataSetChanged();
  }

  public TagFeeds getFeeds() {
    return mFeeds;
  }

  @Override
  public int getCount() {
    return mFeeds.posts.lists == null || mFeeds.posts.lists.size() == 0 ?
        1 : mFeeds.posts.lists.size() + 2; // top/bot padding item
  }

  @Override
  public BaseItem getItem(int position) {
    if (position < 1 || position > mFeeds.posts.lists.size()) {
      return null;
    }
    return mFeeds.posts.lists.get(position - 1);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_PLACEHOLDER:
        convertView = getPlaceHolderView(position, convertView, parent);
        break;
      case TYPE_POST:
        convertView = getPostView(position, convertView, parent);
        break;
    }

    return convertView;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0 || position == getCount() - 1) {
      return TYPE_PLACEHOLDER;
    } else {
      final int type = getItem(position).type;
      switch (type) {
        case BaseItem.TYPE_POST:
          return TYPE_POST;
      }

    }
    return TYPE_PLACEHOLDER;
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }

  @Subscribe
  public void onPostEvent(Post post) {
    for (BaseItem item : mFeeds.posts.lists) {
      if (item == null || !(item instanceof Post)) continue;
      Post p = ((Post) item);
      if (p.equals(post)) {
        p.praise = post.praise;
        p.praised = post.praised;
        p.comments = post.comments;
        notifyDataSetChanged();
        break;
      }
    }
  }

  private View getPostView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPostView(parent.getContext());
    }

    FeedPostView feedPostView = (FeedPostView) convertView;
    feedPostView.setData((Post) getItem(position));

    return convertView;
  }

  private View getPlaceHolderView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new View(parent.getContext());
    }
    if (position == getCount() - 1) {
      convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, U.dp2px(48)));
    } else {
      convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, U.dp2px(0)));
    }
    return convertView;
  }

  private void animateConvertView(int position, View convertView) {
    if (position > 5 && !mAnimated.get(position, false)) {
      AnimatorSet set = new AnimatorSet();
      set.playTogether(
          ObjectAnimator.ofFloat(convertView, "translationY", U.dp2px(350), 0),
          ObjectAnimator.ofFloat(convertView, "rotationX", 5, 0)
      );
      set.setDuration(300);
      set.start();
      mAnimated.put(position, true);
    } else {
      ViewHelper.setTranslationY(convertView, 0);
      ViewHelper.setRotationX(convertView, 0);
    }
  }
}
