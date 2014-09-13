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
import com.utree.eightysix.app.feed.event.UploadClickedEvent;
import com.utree.eightysix.data.*;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 */
class FeedAdapter extends BaseAdapter {

  private static final int TIP_NOT_SHOWN = -1;

  public static final int TYPE_COUNT = 11;
  static final int TYPE_PLACEHOLDER = 0;
  static final int TYPE_UNLOCK = 1;
  static final int TYPE_UPLOAD = 2;
  static final int TYPE_SELECT = 3;
  static final int TYPE_UNKNOWN = 4;
  static final int TYPE_POST = 5;
  static final int TYPE_PROMO = 6;
  static final int TYPE_QUESTION = 7;
  static final int TYPE_INVITE_FRIEND = 8;
  static final int TYPE_INVITE_FACTORY = 9;
  static final int TYPE_OPTION_SET = 10;

  private SparseBooleanArray mAnimated = new SparseBooleanArray();

  private Feeds mFeeds;

  private int mTipOverlaySourcePosition = TIP_NOT_SHOWN;
  private int mTipOverlayPraisePosition = TIP_NOT_SHOWN;
  private int mTipOverlaySharePosition = TIP_NOT_SHOWN;
  private int mTipOverlayRepostPosition = TIP_NOT_SHOWN;

  FeedAdapter(Feeds feeds) {
    mFeeds = feeds;

    if (mFeeds.selectFactory != 1) {
      // 设置在职企业
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_SELECT));
    } else if (mFeeds.upContact != 1) {
      // 上传通讯录
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_UPLOAD));
    } else if (mFeeds.current != 1) {
      // 不在职
      if (mFeeds.circle.friendCount != 0 && mFeeds.lock == 1) {
        // 有朋友但没达到解锁条件
        mFeeds.posts.lists.add(0, new BaseItem(TYPE_UNLOCK));
      }
    } else if (mFeeds.posts.lists.size() == 0) {
      // 邀请厂里的人加入
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_INVITE_FACTORY));
    } else if (mFeeds.circle.friendCount == 0) {
      // 邀请朋友加入
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_INVITE_FRIEND));
    } else if (mFeeds.lock == 1) {
      // 锁定
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_UNLOCK));
    }
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

  public Feeds getFeeds() {
    return mFeeds;
  }

  @Override
  public int getCount() {
    return mFeeds.posts.lists == null ? 0 : mFeeds.posts.lists.size() + 2; // top/bot padding item
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
      case TYPE_UNLOCK:
        convertView = getUnlockView(convertView, parent);
        break;
      case TYPE_UPLOAD:
        convertView = getUploadView(convertView, parent);
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
      case TYPE_INVITE_FACTORY:
        convertView = getInviteFactoryView(convertView, parent);
        break;
      case TYPE_INVITE_FRIEND:
        convertView = getInviteFriendView(convertView, parent);
        break;
      case TYPE_OPTION_SET:
        convertView = getOptionSetView(position, convertView, parent);
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
        case BaseItem.TYPE_OPTION_SET:
          return TYPE_OPTION_SET;
        case TYPE_UPLOAD:
        case TYPE_UNLOCK:
        case TYPE_SELECT:
        case TYPE_INVITE_FACTORY:
        case TYPE_INVITE_FRIEND:
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

  void showTipOverlaySource(int position) {
    mTipOverlaySourcePosition = position;
    notifyDataSetChanged();
  }

  void showTipOverlayPraise(int position) {
    mTipOverlayPraisePosition = position;
    notifyDataSetChanged();
  }

  void showTipOverlayShare(int position) {
    mTipOverlaySharePosition = position;
    notifyDataSetChanged();
  }

  void showTipOverlayRepost(int position) {
    mTipOverlayRepostPosition = position;
    notifyDataSetChanged();
  }

  @Subscribe
  public void onDismissTipOverlay(DismissTipOverlayEvent event) {
    switch (event.getType()) {
      case DismissTipOverlayEvent.TYPE_PRAISE:
        mTipOverlayPraisePosition = TIP_NOT_SHOWN;
        break;
      case DismissTipOverlayEvent.TYPE_SHARE:
        mTipOverlaySharePosition = TIP_NOT_SHOWN;
        break;
      case DismissTipOverlayEvent.TYPE_SOURCE:
        mTipOverlaySourcePosition = TIP_NOT_SHOWN;
        break;
      case DismissTipOverlayEvent.TYPE_REPOST:
        mTipOverlayRepostPosition = TIP_NOT_SHOWN;
        break;
    }
  }

  boolean tipsShowing() {
    return mTipOverlayPraisePosition != -1 || mTipOverlaySharePosition != -1 || mTipOverlaySourcePosition != -1 ||
        mTipOverlayRepostPosition != -1;
  }

  private View getPostView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPostView(parent.getContext());
    }

    FeedPostView feedPostView = (FeedPostView) convertView;
    feedPostView.setData((Post) getItem(position));

    if (mTipOverlaySourcePosition == position) {
      feedPostView.showSourceTipOverlay();
    } else if (mTipOverlaySourcePosition == -1 && mTipOverlayPraisePosition == position) {
      feedPostView.showPraiseTipOverlay();
    } else if (mTipOverlayPraisePosition == -1 && mTipOverlaySharePosition == position) {
      feedPostView.showShareTipOverlay();
    } else if (mTipOverlaySharePosition == -1 && mTipOverlayRepostPosition == position) {
      feedPostView.showRepostTipOverlay();
    } else {
      feedPostView.hidePraiseTipOverlay();
      feedPostView.hideSourceTipOverlay();
      feedPostView.hideShareTipOverlay();
      feedPostView.hideRepostTipOverlay();
    }

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

  private View getUploadView(View convertView, ViewGroup parent) {
    UploadViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload, parent, false);
      holder = new UploadViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (UploadViewHolder) convertView.getTag();
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
        U.getAnalyser().trackEvent(U.getContext(), "feed_unlock", "feed_unlock");
        U.getBus().post(new UnlockClickedEvent());
      }
    });

    holder.mIvHiddenCount.setVisibility(View.GONE);
    holder.mRbHidden.setText(String.valueOf(mFeeds.hiddenCount + 1));
    holder.mTvHidden.setText(U.gfs(R.string.hidden_friends_feed, mFeeds.hiddenCount + 1));
    holder.mRbUnlock.setText(U.gs(R.string.unlock_to_view));

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

  private View getInviteFriendView(View convertView, final ViewGroup parent) {
    InviteFriendViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_friend, parent, false);
      holder = new InviteFriendViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (InviteFriendViewHolder) convertView.getTag();
    }

    return convertView;
  }

  private View getInviteFactoryView(View convertView, final ViewGroup parent) {
    InviteFactoryViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_factory, parent, false);
      holder = new InviteFactoryViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (InviteFactoryViewHolder) convertView.getTag();
    }

    return convertView;
  }

  private View getOptionSetView(int position, View convertView, final ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedOptionSetView(parent.getContext());
    }

    ((FeedOptionSetView) convertView).setData(mFeeds.circle.id, (OptionSet) getItem(position));
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

  @Keep
  static class SelectViewHolder {

    private int mId;

    public SelectViewHolder(View view, int id) {
      mId = id;
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.rb_select)
    public void onRbSelectClicked(View view) {
      U.getAnalyser().trackEvent(U.getContext(), "feed_select", "feed_select");
      BaseCirclesActivity.startSelect(view.getContext());
    }
  }

  @Keep
  static class UploadViewHolder {

    public UploadViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.rb_invite)
    public void onRbInviteClicked() {
      U.getAnalyser().trackEvent(U.getContext(), "feed_upload", "feed_upload");
      U.getBus().post(new UploadClickedEvent());
    }
  }

  @Keep
  static class UnlockViewHolder {
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

  static class InviteFriendViewHolder {
    @OnClick (R.id.rb_invite)
    public void onRbInviteClicked() {
      U.getAnalyser().trackEvent(U.getContext(), "feed_invite_friend", "feed_invite_friend");
      U.getBus().post(new InviteClickedEvent());
    }

    public InviteFriendViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  static class InviteFactoryViewHolder {
    @OnClick (R.id.rb_invite)
    public void onRbInviteClicked() {
      U.getAnalyser().trackEvent(U.getContext(), "feed_invite_factory", "feed_invite_factory");
      U.getBus().post(new InviteClickedEvent());
    }

    public InviteFactoryViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  static class DismissTipOverlayEvent {
    private int mType;

    static final int TYPE_SOURCE = 1;
    static final int TYPE_PRAISE = 2;
    static final int TYPE_SHARE = 3;
    static final int TYPE_REPOST = 4;

    DismissTipOverlayEvent(int type) {
      mType = type;
    }

    public int getType() {
      return mType;
    }
  }
}
