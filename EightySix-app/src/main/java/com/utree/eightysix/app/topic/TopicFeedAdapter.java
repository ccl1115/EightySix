package com.utree.eightysix.app.topic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedPostView;
import static com.utree.eightysix.app.topic.TopicActivity.TAB_FEATURE;
import static com.utree.eightysix.app.topic.TopicActivity.TAB_NEW;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TopicFeedAdapter extends BaseAdapter {

  private static final int TYPE_COUNT = 3;

  private static final int TYPE_TOPIC = 0;
  private static final int TYPE_POST = 1;
  private static final int TYPE_EMPTY = 2;

  private Topic mTopic;

  private List<Post> mNewPosts = new ArrayList<Post>();
  private List<Post> mFeaturePosts = new ArrayList<Post>();

  private int mTab;

  private View mTopicView;

  private TopicViewHolder mTopicViewHolder;

  private Callback mCallback;

  private boolean mShowFeatureEmptyView;
  private boolean mShowNewEmptyView;

  public TopicFeedAdapter(Topic topic) {
    mTopic = topic;
    mTopicView = LayoutInflater.from(U.getContext()).inflate(R.layout.item_topic, null, false);
    mTopicViewHolder = new TopicViewHolder(mTopicView);

    switchTab(TAB_NEW);
  }

  public void showFeatureEmptyView(boolean show) {
    mShowFeatureEmptyView = show;
    notifyDataSetChanged();
  }

  public void showNewEmptyView(boolean show) {
    mShowNewEmptyView = show;
    notifyDataSetChanged();
  }

  public List<Post> getNewPosts() {
    return mNewPosts;
  }

  public List<Post> getFeaturePosts() {
    return mFeaturePosts;
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public void setTopic(Topic topic) {
    mTopic = topic;
    notifyDataSetChanged();
  }

  public void add(int tab, List<Post> posts) {
    switch (tab) {
      case TAB_NEW:
        mNewPosts.addAll(posts);
        break;
      case TAB_FEATURE:
        mFeaturePosts.addAll(posts);
        break;
    }
    notifyDataSetChanged();
  }

  public void switchTab(int tab) {
    mTab = tab;
    switch (mTab) {
      case TAB_NEW:
        mTopicViewHolder.mTvTabLeft.setTextColor(U.getContext().getResources().getColor(R.color.apptheme_primary_light_color));
        mTopicViewHolder.mVTabLeft.setVisibility(View.VISIBLE);
        mTopicViewHolder.mTvTabRight.setTextColor(U.getContext().getResources().getColor(R.color.apptheme_primary_grey_color_pressed));
        mTopicViewHolder.mVTabRight.setVisibility(View.INVISIBLE);
        break;
      case TAB_FEATURE:
        mTopicViewHolder.mTvTabRight.setTextColor(U.getContext().getResources().getColor(R.color.apptheme_primary_light_color));
        mTopicViewHolder.mVTabRight.setVisibility(View.VISIBLE);
        mTopicViewHolder.mTvTabLeft.setTextColor(U.getContext().getResources().getColor(R.color.apptheme_primary_grey_color_pressed));
        mTopicViewHolder.mVTabLeft.setVisibility(View.INVISIBLE);
        break;
    }

    notifyDataSetChanged();
  }

  public int getCurrentTab() {
    return mTab;
  }

  @Override
  public int getCount() {
    switch (mTab) {
      case TAB_NEW:
        return 1 + Math.max(1, mNewPosts.size());
      case TAB_FEATURE:
        return 1 + Math.max(1, mFeaturePosts.size());
    }
    return 0;
  }

  @Override
  public Object getItem(int position) {
    if (position == 0) {
      return mTopic;
    } else {
      switch (mTab) {
        case TAB_NEW:
          if (mNewPosts.size() > 0) return mNewPosts.get(position - 1);
        case TAB_FEATURE:
          if (mFeaturePosts.size()> 0) return mFeaturePosts.get(position - 1);
      }
    }
    return null;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_TOPIC:
        return getTopicView(position, convertView, parent);
      case TYPE_POST:
        return getPostView(position, convertView, parent);
      case TYPE_EMPTY:
        return getEmptyView(convertView, parent);
    }
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return TYPE_TOPIC;
    } else if (mTab == TAB_NEW && mNewPosts.size() == 0) {
      return TYPE_EMPTY;
    } else if (mTab == TAB_FEATURE && mFeaturePosts.size() == 0) {
      return TYPE_EMPTY;
    } else {
      return TYPE_POST;
    }
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }

  private View getTopicView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = mTopicView;
    }

    final Topic topic = (Topic) getItem(position);

    if (topic != null) {
      mTopicViewHolder.mTvFeedCount.setText(topic.postCount + "条内容");
      mTopicViewHolder.mTvText.setText(topic.content);
      mTopicViewHolder.mLlTop.setBackgroundColor(ColorUtil.strToColor(topic.bgColor));

      List<Tag> tags = topic.tags;
      for (int i = 0; i < tags.size(); i++) {
        Tag g = tags.get(i);
        switch (i) {
          case 0:
            mTopicViewHolder.mTvTag1.setText("#" + g.content);
            break;
          case 1:
            mTopicViewHolder.mTvTag2.setText("#" + g.content);
            break;
          case 2:
            mTopicViewHolder.mTvTag3.setText("#" + g.content);
            break;

        }
      }
    }

    return convertView;
  }

  private View getPostView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPostView(parent.getContext());
    }

    if (position == getCount() - 1) {
      convertView.setPadding(0, 0, 0, U.dp2px(8));
    } else {
      convertView.setPadding(0, 0, 0, 0);
    }

    ((FeedPostView) convertView).setData((Post) getItem(position), 0);

    return convertView;
  }

  private View getEmptyView(View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new RandomSceneTextView(parent.getContext());
    }

    if (((mTab == TAB_FEATURE) && mShowFeatureEmptyView)
        || ((mTab == TAB_NEW) && mShowNewEmptyView)) {
      convertView.setVisibility(View.VISIBLE);
    } else {
      convertView.setVisibility(View.INVISIBLE);
    }

    if (mTab == TAB_NEW) {
      ((RandomSceneTextView) convertView).setText("这个话题下还没有帖子哟");
      ((RandomSceneTextView) convertView).setSubText("抢先发帖，或去其他的话题看看吧");
    } else if (mTab == TAB_FEATURE) {
      ((RandomSceneTextView) convertView).setText("这个话题下还没有精选帖哟");
      ((RandomSceneTextView) convertView).setSubText("快快顶帖，或去其他的话题看看吧");
    }

    return convertView;
  }

  public interface Callback {
    void onTabLeftClicked();

    void onTabRightClicked();

    void onSendClicked();
  }

  class TopicViewHolder {

    @InjectView (R.id.tv_tag_1)
    public TextView mTvTag1;

    @InjectView (R.id.tv_tag_2)
    public TextView mTvTag2;

    @InjectView (R.id.tv_tag_3)
    public TextView mTvTag3;

    @InjectView (R.id.tv_text)
    public TextView mTvText;

    @InjectView (R.id.tv_feed_count)
    public TextView mTvFeedCount;

    @InjectView (R.id.tv_tab_left)
    public TextView mTvTabLeft;

    @InjectView (R.id.v_tab_left)
    public View mVTabLeft;

    @InjectView (R.id.tv_tab_right)
    public TextView mTvTabRight;

    @InjectView (R.id.v_tab_right)
    public View mVTabRight;

    @InjectView (R.id.ll_top)
    public LinearLayout mLlTop;

    TopicViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.tv_tab_left)
    public void onTvTabLeftClicked() {
      switchTab(TAB_NEW);

      if (mCallback != null) {
        mCallback.onTabLeftClicked();
      }
    }

    @OnClick (R.id.tv_tab_right)
    public void onTvTabRightClicked() {
      switchTab(TAB_FEATURE);

      if (mCallback != null) {
        mCallback.onTabRightClicked();
      }
    }

    @OnClick (R.id.ib_send)
    public void onIbSendClicked() {

      if (mCallback != null) {
        mCallback.onSendClicked();
      }
    }
  }

}
