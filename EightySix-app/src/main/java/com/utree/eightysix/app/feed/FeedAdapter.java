package com.utree.eightysix.app.feed;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.data.*;
import com.utree.eightysix.widget.RoundedButton;
import java.util.List;

/**
 */
class FeedAdapter extends BaseAdapter {

  public static final int TYPE_COUNT = 8;
  private static final int TYPE_PLACEHOLDER = 0;
  private static final int TYPE_UNLOCK = 1;
  private static final int TYPE_INVITE = 2;
  private static final int TYPE_SELECT = 3;
  private static final int TYPE_UNKNOWN = 4;
  private static final int TYPE_POST = 5;
  private static final int TYPE_PROMO = 6;
  private static final int TYPE_QUESTION = 7;

  private SparseBooleanArray mAnimated = new SparseBooleanArray();

  private Feeds mFeeds;

  FeedAdapter(Feeds feeds) {
    mFeeds = feeds;

    boolean showUnlock = mFeeds.lock == 1;
    boolean showInvite = mFeeds.upContact != 1;
    boolean showSelect = mFeeds.selectFactory != 1;
    if (showUnlock) {
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_UNLOCK));
    }
    if (showInvite && showSelect) {
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_INVITE));
    } else if (!showInvite && showSelect) {
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_SELECT));
    }
  }

  public void add(List<BaseItem> posts) {
    if (mFeeds.posts.lists == null) {
      mFeeds.posts.lists = posts;
    } else {
      mFeeds.posts.lists.addAll(posts);
    }
    notifyDataSetChanged();
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

  public Feeds getFeeds() {
    return mFeeds;
  }

  @Override
  public int getCount() {
    return mFeeds.posts.lists == null ? 0 : mFeeds.posts.lists.size() + 2; // top/bot padding item
  }

  @Override
  public BaseItem getItem(int position) {
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
      case TYPE_UNLOCK:
        convertView = getUnlockView(convertView, parent);
        break;
      case TYPE_INVITE:
        convertView = getInviteView(convertView, parent);
        break;
      case TYPE_SELECT:
        convertView = getSelectView(convertView, parent);
        break;
      case TYPE_PROMO:
        convertView = getPromoView(position, convertView, parent);
        break;
      case TYPE_QUESTION:
        convertView = getQuestionView(position, convertView, parent);
        break;
      case TYPE_UNKNOWN:
        convertView = getUnknownView(convertView, parent);
        break;
    }

    animateConvertView(position, convertView);

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
        case BaseItem.TYPE_PROMOTION:
          return TYPE_PROMO;
        case BaseItem.TYPE_QUESTION_SET:
          return TYPE_QUESTION;
        case TYPE_INVITE:
        case TYPE_UNLOCK:
        case TYPE_SELECT:
          return type;
        default:
          return TYPE_UNKNOWN;
      }
    }
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

    ((FeedPostView) convertView).setData((Post) getItem(position));
    return convertView;
  }

  private View getPromoView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPromotionView(parent.getContext());
    }

    ((FeedPromotionView) convertView).setData(mFeeds.circle.id, (Promotion) getItem(position));
    return convertView;
  }

  private View getQuestionView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedQuestionView(parent.getContext());
    }

    ((FeedQuestionView) convertView).setData((QuestionSet) getItem(position));
    return convertView;
  }

  private View getUnknownView(View convertView, ViewGroup parent) {
    if (convertView == null) {
      TextView tv = new TextView(parent.getContext());
      tv.setText("未知的条目，请升级客户端");
      convertView = tv;
    }
    return convertView;
  }

  private void animateConvertView(int position, View convertView) {
    if (position > 3 && !mAnimated.get(position, false)) {
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
        U.getAnalyser().trackEvent(U.getContext(), "feed_unlock");
        U.getBus().post(new UnlockClickedEvent());
      }
    });

    if (mFeeds.hiddenCount == 0) {
      holder.mRbHidden.setText("");
      holder.mIvHiddenCount.setVisibility(View.VISIBLE);
      holder.mTvHidden.setText(U.gs(R.string.circle_unlocked_tip));
      holder.mRbUnlock.setText(U.gs(R.string.unlock));
    } else {
      holder.mIvHiddenCount.setVisibility(View.GONE);
      holder.mRbHidden.setText(String.valueOf(mFeeds.hiddenCount));
      holder.mTvHidden.setText(U.gfs(R.string.hidden_friends_feed, mFeeds.hiddenCount));
      holder.mRbUnlock.setText(U.gs(R.string.unlock_to_view));
    }

    return convertView;
  }

  private View getSelectView(View convertView, final ViewGroup parent) {
    SelectViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select, parent, false);
      holder = new SelectViewHolder(convertView, mFeeds.circle.id);
      convertView.setTag(holder);
    } else {
      holder = (SelectViewHolder) convertView.getTag();
    }

    return convertView;
  }

  @Keep
  static class SelectViewHolder {

    private int mId;

    public SelectViewHolder(View view, int id) {
      mId = id;
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.rb_select)
    public void onRbSelectClicked(View view) {
      U.getAnalyser().trackEvent(U.getContext(), "feed_select");
      BaseCirclesActivity.startSelect(view.getContext());
    }
  }

  @Keep
  static class InviteViewHolder {

    public InviteViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.rb_invite)
    public void onRbInviteClicked() {
      U.getAnalyser().trackEvent(U.getContext(), "feed_upload");
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

    @InjectView (R.id.iv_hidden_count)
    public ImageView mIvHiddenCount;

    public UnlockViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
