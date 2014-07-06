package com.utree.eightysix.app.feed;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.data.Feeds;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.widget.RoundedButton;
import java.util.Iterator;
import java.util.List;

/**
 */
class FeedAdapter extends BaseAdapter {

  public static final int TYPE_COUNT = 5;
  private static final int TYPE_POST = 0;
  private static final int TYPE_PLACEHOLDER = 1;
  private static final int TYPE_UNLOCK = 2;
  private static final int TYPE_INVITE = 3;
  private static final int TYPE_SELECT = 4;
  private SparseBooleanArray mAnimated = new SparseBooleanArray();

  private Feeds mFeeds;

  private boolean mShowInvite;
  private boolean mShowUnlock;
  private boolean mShowSelect;

  FeedAdapter(Feeds feeds) {
    mFeeds = feeds;
    mFeeds.posts.lists.add(0, null); // for placeholder view
    mShowUnlock = mFeeds.lock == 1;
    mShowInvite = mFeeds.upContact != 1;
    mShowSelect = mFeeds.selectFactory != 1;
    if (mShowUnlock) {
      mFeeds.posts.lists.add(1, null);
    }
    if (mShowInvite) {
      mFeeds.posts.lists.add(1, null);
    }
    if (mShowSelect) {
      mFeeds.posts.lists.add(1, null);
    }
  }

  public void add(List<Post> posts) {
    if (mFeeds.posts.lists == null) {
      mFeeds.posts.lists = posts;
      mFeeds.posts.lists.add(0, null); // for placeholder view
    } else {
      mFeeds.posts.lists.addAll(posts);
    }
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mFeeds.posts.lists == null ? 0 : mFeeds.posts.lists.size();
  }

  @Override
  public Post getItem(int position) {
    return mFeeds.posts.lists.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_PLACEHOLDER:
        convertView = getPlaceHolderView(convertView, parent);
        break;
      case TYPE_POST:
        convertView = getPostView(position, convertView, parent);
        break;
      case TYPE_UNLOCK:
        convertView = getUnlockView(convertView, parent);
        break;
      case TYPE_INVITE:
        convertView = getInviteView(convertView, parent);
        break;
      case TYPE_SELECT:
        convertView = getSelectView(convertView, parent);
        break;
    }
    return convertView;
  }

  @Override
  public int getItemViewType(int position) {
    switch (position) {
      case 0:
        return TYPE_PLACEHOLDER;
      case 1:
        if (mShowInvite) return TYPE_INVITE;
        else if (mShowUnlock) return TYPE_UNLOCK;
        else if (mShowSelect) return TYPE_SELECT;
        else return TYPE_POST;
      case 2:
        if (mShowInvite && mShowUnlock) return TYPE_UNLOCK;
        else if (mShowInvite && mShowSelect) return TYPE_SELECT;
        else if (mShowUnlock && mShowSelect) return TYPE_SELECT;
        else return TYPE_POST;
      case 3:
        if (mShowInvite && mShowUnlock && mShowSelect) return TYPE_SELECT;
        else return TYPE_POST;
      default:
        return TYPE_POST;
    }
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }

  private View getPostView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPostView(parent.getContext());
    }

    if (!mAnimated.get(position, false)) {
      AnimatorSet set = new AnimatorSet();
      set.playTogether(
          ObjectAnimator.ofFloat(convertView, "translationY", U.dp2px(350), 0),
          ObjectAnimator.ofFloat(convertView, "rotationX", 15, 0)
      );
      set.setDuration(500);
      set.start();
      mAnimated.put(position, true);
    } else {
      ViewHelper.setTranslationY(convertView, 0);
      ViewHelper.setRotationX(convertView, 0);
    }

    FeedPostView feedPostView = (FeedPostView) convertView;
    feedPostView.setData(getItem(position));
    return convertView;
  }

  private View getPlaceHolderView(View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new View(parent.getContext());
      convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, U.dp2px(0)));
    }
    return convertView;
  }

  private View getInviteView(View convertView, ViewGroup parent) {
    InviteViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite, parent, false);
      holder = new InviteViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (InviteViewHolder) convertView.getTag();
    }

    return convertView;
  }

  private View getUnlockView(View convertView, final ViewGroup parent) {
    UnlockViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unlock, parent, false);
      holder = new UnlockViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (UnlockViewHolder) convertView.getTag();
    }

    holder.mRbUnlock.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        U.getBus().post(new UnlockClickedEvent());
      }
    });

    holder.mRbHidden.setText(String.valueOf(mFeeds.hiddenCount));
    holder.mTvHidden.setText(String.format(parent.getContext().getString(R.string.hidden_friends_feed), mFeeds.hiddenCount));

    return convertView;
  }

  private View getSelectView(View convertView, final ViewGroup parent) {
    SelectViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select, parent, false);
      holder = new SelectViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (SelectViewHolder) convertView.getTag();
    }
    return convertView;
  }

  @Subscribe
  public void onPostEvent(Post post) {
    for (Post p : mFeeds.posts.lists) {
      if (p == null) continue;
      if (p.equals(post)) {
        p.praise = post.praise;
        p.praised = post.praised;
        p.comments = post.comments;
        U.getBus().post(new AdapterDataSetChangedEvent());
        break;
      }
    }
  }

  @Subscribe
  public void onPostDeleteEvent(PostDeleteEvent event) {
    for (Iterator<Post> iterator = mFeeds.posts.lists.iterator(); iterator.hasNext(); ) {
      Post p = iterator.next();
      if (p == null) continue;
      if (p.equals(event.getPost())) {
        iterator.remove();
        U.getBus().post(new AdapterDataSetChangedEvent());
        break;
      }
    }
  }

  @Keep
  static class SelectViewHolder {

    public SelectViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.rb_select)
    public void onRbSelectClicked(View view) {

    }
  }

  @Keep
  static class InviteViewHolder {

    public InviteViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.rb_invite)
    public void onRbInviteClicked() {
      U.getBus().post(new InviteClickedEvent());
    }
  }

  @Keep
  public static class UnlockViewHolder {
    @InjectView (R.id.rb_unlock)
    public RoundedButton mRbUnlock;

    @InjectView (R.id.tv_hidden_count)
    public TextView mTvHidden;

    @InjectView (R.id.rb_hidden_count)
    public RoundedButton mRbHidden;

    public UnlockViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
