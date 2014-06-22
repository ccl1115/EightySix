package com.utree.eightysix.app.feed;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.FeedPostView;
import com.utree.eightysix.widget.RoundedButton;
import java.util.List;

/**
 */
class FeedAdapter extends BaseAdapter {

  static final int TYPE_POST = 0;
  private static final int TYPE_PLACEHOLDER = 1;
  private static final int TYPE_UNLOCK = 2;
  private static final int TYPE_INVITE = 3;

  private SparseBooleanArray mAnimated = new SparseBooleanArray();

  private List<Post> mPosts;

  private boolean mShowInvite;
  private boolean mShowUnlock = true;

  FeedAdapter(List<Post> posts) {
    mPosts = posts;
    mPosts.add(0, null); // for placeholder view
    if (mShowUnlock) {
      mPosts.add(1, null);
    }
    if (mShowInvite) {
      mPosts.add(1, null);
    }
  }

  public void add(List<Post> posts) {
    if (mPosts == null) {
      mPosts = posts;
      mPosts.add(0, null); // for placeholder view
      if (mShowUnlock) {
        mPosts.add(1, null);
      }
      if (mShowInvite) {
        mPosts.add(1, null);
      }
    } else {
      mPosts.addAll(posts);
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
        else return TYPE_POST;
      case 2:
        if (mShowInvite && mShowUnlock) return TYPE_UNLOCK;
        else return TYPE_POST;
      default:
        return TYPE_POST;
    }
  }

  @Override
  public int getViewTypeCount() {
    return 4;
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
      set.setDuration(600);
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
      convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          parent.getResources().getDimensionPixelOffset(R.dimen.activity_top_bar_height)));
    }
    return convertView;
  }

  private View getInviteView(View convertView, ViewGroup parent) {
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

    View.OnClickListener listener = (View.OnClickListener) holder.mRbUnlock.getTag();
    if (listener == null) {
      listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(parent.getContext(), "TODO invite friends", Toast.LENGTH_SHORT).show();
        }
      };
      holder.mRbUnlock.setTag(listener);
    }
    holder.mRbUnlock.setOnClickListener(listener);

    holder.mRbHidden.setText("22");
    holder.mTvHidden.setText(String.format(parent.getContext().getString(R.string.hidden_friends_feed), 22));

    return convertView;
  }

  @Keep
  public static class UnlockViewHolder {
    @InjectView(R.id.rb_unlock)
    public RoundedButton mRbUnlock;

    @InjectView(R.id.tv_hidden_count)
    public TextView mTvHidden;

    @InjectView(R.id.rb_hidden_count)
    public RoundedButton mRbHidden;

    public UnlockViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
