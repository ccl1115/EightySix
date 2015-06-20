package com.utree.eightysix.app.region;

import android.content.Intent;
import android.text.TextUtils;
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
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.account.AddFriendActivity;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.feed.*;
import com.utree.eightysix.app.feed.event.UploadClickedEvent;
import com.utree.eightysix.data.*;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.utils.CmdHandler;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 */
public class FeedRegionAdapter extends BaseAdapter {

  /**
   * TIP_NOT_SHOWN
   */
  protected static final int TNS = -1;

  protected final int TYPE_COUNT = 15;
  public static final int TYPE_PLACEHOLDER = 0;
  public static final int TYPE_UNLOCK = 1;
  public static final int TYPE_UPLOAD = 2;
  public static final int TYPE_SELECT = 3;
  public static final int TYPE_UNKNOWN = 4;
  public static final int TYPE_POST = 5;
  public static final int TYPE_PROMO = 6;
  public static final int TYPE_QUESTION = 7;
  public static final int TYPE_INVITE_FRIEND = 8;
  public static final int TYPE_INVITE_FACTORY = 9;
  public static final int TYPE_OPTION_SET = 10;
  public static final int TYPE_TOPIC = 11;
  public static final int TYPE_FEED_INTENT = 12;
  public static final int TYPE_BAINIAN = 13;
  public static final int TYPE_SIGN = 14;

  protected Feeds mFeeds;

  protected int mTipSourcePosition = TNS;
  protected int mTipPraisePosition = TNS;
  protected int mTipRepostPosition = TNS;
  protected int mTipTempNamePosition = TNS;
  protected int mTipTagsPosition = TNS;

  public FeedRegionAdapter(FeedsByRegion feeds, FeedsByRegionResponse.Extra extra) {
    this(feeds);
    if (feeds.circle != null && feeds.current == 1 && extra != null) {
      mFeeds.posts.lists.add(0, new FeedSign(extra));
    }
  }

  public FeedRegionAdapter(FeedsByRegion feeds) {
    mFeeds = feeds;

    if (mFeeds.selectFactory != 1) {
      // 设置在职企业
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_SELECT));
    } else if (mFeeds.upContact != 1) {
      // 上传通讯录
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_UPLOAD));
    } else {
      boolean hasPosts = false;
      for (BaseItem item : mFeeds.posts.lists) {
        if (item.type == BaseItem.TYPE_POST) {
          hasPosts = true;
          break;
        }
      }
      if (!hasPosts) {
        // 邀请厂里的人加入
        mFeeds.posts.lists.add(0, new BaseItem(TYPE_INVITE_FACTORY));
      }
    }
  }

  public FeedRegionAdapter() {

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

  @Subscribe
  public void onOptionSetEvent(OptionSet optionSet) {
    List<BaseItem> lists = mFeeds.posts.lists;
    for (int i = 0; i < lists.size(); i++) {
      BaseItem item = lists.get(i);
      if (item.type == BaseItem.TYPE_OPTION_SET) {
        lists.set(i, optionSet);
        break;
      }
    }
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
      case TYPE_TOPIC:
        convertView = getTopicView(position, convertView, parent);
        break;
      case TYPE_FEED_INTENT:
        convertView = getFeedIntentView(position, convertView, parent);
        break;
      case TYPE_BAINIAN:
        convertView = getBainianView(position, convertView, parent);
        break;
      case TYPE_SIGN:
        convertView = getSignView(position, convertView, parent);
        break;
    }

    return convertView;
  }

  private View getSignView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedSignView(parent.getContext());
    }

    ((FeedSignView) convertView).setData((FeedSign) getItem(position), mFeeds.circle.id);

    return convertView;
  }

  private View getBainianView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedBainianView(parent.getContext());
    }

    ((FeedBainianView) convertView).setData(((Bainian) getItem(position)));

    return convertView;
  }

  private View getFeedIntentView(int position, View convertView, ViewGroup parent) {
    FeedIntentViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_intent, parent, false);
      holder = new FeedIntentViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (FeedIntentViewHolder) convertView.getTag();
    }


    holder.setFeedIntent((FeedIntent) getItem(position));

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
        case BaseItem.TYPE_TOPIC:
          return TYPE_TOPIC;
        case BaseItem.TYPE_FEED_INTENT:
          return TYPE_FEED_INTENT;
        case BaseItem.TYPE_BAINIAN:
          return TYPE_BAINIAN;
        case BaseItem.TYPE_SIGN:
          return TYPE_SIGN;
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

  public void showTipSource(int position) {
    mTipSourcePosition = position;
    notifyDataSetChanged();
  }

  public void showTipPraise(int position) {
    mTipPraisePosition = position;
    notifyDataSetChanged();
  }

  public void showTipShare(int position) {
    notifyDataSetChanged();
  }

  public void showTipRepost(int position) {
    mTipRepostPosition = position;
    notifyDataSetChanged();
  }

  public void showTipTempName(int position) {
    mTipTempNamePosition = position;
    notifyDataSetChanged();
  }

  public void showTipTags(int position) {
    mTipTagsPosition = position;
    notifyDataSetChanged();
  }

  @Subscribe
  public void onDismissTipOverlay(DismissTipOverlayEvent event) {
    switch (event.getType()) {
      case DismissTipOverlayEvent.TYPE_PRAISE:
        mTipPraisePosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_SOURCE:
        mTipSourcePosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_REPOST:
        mTipRepostPosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_TAGS:
        mTipTagsPosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_TEMP_NAME:
        mTipTempNamePosition = TNS;
        break;
    }
  }

  public boolean tipsShowing() {
    return mTipPraisePosition != TNS
        || mTipSourcePosition != TNS
        || mTipRepostPosition != TNS
        || mTipTagsPosition != TNS
        || mTipTempNamePosition != TNS;
  }

  private View getPostView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPostView(parent.getContext());
    }

    FeedPostView feedPostView = (FeedPostView) convertView;
    final Post item = (Post) getItem(position);

    feedPostView.setData(item);

    if (mTipTempNamePosition == position) {
      feedPostView.showTempNameTip();
    } else if (mTipTempNamePosition == TNS && mTipSourcePosition == position) {
      feedPostView.showSourceTip();
    } else if (mTipSourcePosition == TNS && mTipPraisePosition == position) {
      feedPostView.showPraiseTip();
    } else if (mTipRepostPosition == TNS && mTipTagsPosition == position) {
      feedPostView.showTagsTip();
    } else {
      feedPostView.hidePraiseTip();
      feedPostView.hideSourceTip();
      feedPostView.hideRepostTip();
      feedPostView.hideTempNameTip();
      feedPostView.hideTagsTip();
    }

    return convertView;
  }

  private View getPromoView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPromotionView(parent.getContext());
    }

    ((FeedPromotionView) convertView).setData((Promotion) getItem(position));
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
      convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          parent.getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height) + U.dp2px(36)));
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
        v.getContext().startActivity(new Intent(v.getContext(), AddFriendActivity.class));
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
      holder = new SelectViewHolder(convertView);
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

  private View getTopicView(int position, View convertView, final ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedTopicView(parent.getContext());
    }

    ((FeedTopicView) convertView).setData((PostTopic) getItem(position));

    return convertView;
  }

  @Keep
  static class SelectViewHolder {

    public SelectViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.rb_select)
    public void onRbSelectClicked(View view) {
      U.getAnalyser().trackEvent(U.getContext(), "feed_select", "feed_select");
      BaseCirclesActivity.startSelect(view.getContext(), false);
    }
  }

  @Keep
  static class UploadViewHolder {

    public UploadViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.rb_invite)
    public void onRbInviteClicked() {
      U.getAnalyser().trackEvent(U.getContext(), "feed_upload", "feed_upload");
      U.getBus().post(new UploadClickedEvent());
    }
  }

  @Keep
  static class UnlockViewHolder {
    @InjectView(R.id.rb_unlock)
    public RoundedButton mRbUnlock;

    @InjectView(R.id.tv_hidden_count)
    public TextView mTvHidden;

    @InjectView(R.id.rb_hidden_count)
    public RoundedButton mRbHidden;

    @InjectView(R.id.iv_hidden_count)
    public ImageView mIvHiddenCount;

    public UnlockViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  static class InviteFriendViewHolder {
    @OnClick(R.id.rb_invite)
    public void onRbInviteClicked(View v) {
      U.getAnalyser().trackEvent(v.getContext(), "feed_invite_friend", "feed_invite_friend");
      v.getContext().startActivity(new Intent(v.getContext(), AddFriendActivity.class));
    }

    public InviteFriendViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  static class InviteFactoryViewHolder {
    @OnClick(R.id.rb_invite)
    public void onRbInviteClicked(View v) {
      U.getAnalyser().trackEvent(U.getContext(), "feed_invite_factory", "feed_invite_factory");
      v.getContext().startActivity(new Intent(v.getContext(), AddFriendActivity.class));
    }

    public InviteFactoryViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  static class FeedIntentViewHolder {

    FeedIntent mFeedIntent;

    @InjectView(R.id.iv_bg)
    public ImageView mIvBg;

    public FeedIntentViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.iv_bg)
    public void onIvBgClicked(View v) {
      new CmdHandler().handle(v.getContext(), mFeedIntent.appIntent.cmd);
    }

    public void setFeedIntent(FeedIntent data) {
      mFeedIntent = data;

      if (!TextUtils.isEmpty(mFeedIntent.bgUrl)) {
        Picasso.with(U.getContext()).load(mFeedIntent.bgUrl).into(mIvBg);
      }
    }
  }

  public static class DismissTipOverlayEvent {
    private int mType;

    public static final int TYPE_SOURCE = 1;
    public static final int TYPE_PRAISE = 2;
    public static final int TYPE_REPOST = 4;
    public static final int TYPE_TEMP_NAME = 5;
    public static final int TYPE_TAGS = 6;

    public DismissTipOverlayEvent(int type) {
      mType = type;
    }

    public int getType() {
      return mType;
    }
  }
}
