package com.utree.eightysix.app.topic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.feed.FeedPostView;
import static com.utree.eightysix.app.topic.TopicActivity.TAB_FEATURE;
import static com.utree.eightysix.app.topic.TopicActivity.TAB_NEW;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TopicFeedAdapter extends BaseAdapter {

  private static final int TYPE_COUNT = 2;

  private static final int TYPE_TOPIC = 0;
  private static final int TYPE_POST = 1;

  private Topic mTopic;
  private List<Post> mNewPosts = new ArrayList<Post>();
  private List<Post> mFeaturePosts = new ArrayList<Post>();

  private int mTab;

  public TopicFeedAdapter(Topic topic) {
    mTopic = topic;
  }

  public void add(int tab, List<Post> posts) {
    switch (tab) {
      case TAB_NEW:
        mNewPosts.addAll(posts);
      case TAB_FEATURE:
        mFeaturePosts.addAll(posts);
    }
    notifyDataSetChanged();
  }

  public void switchTab(int tab) {
    mTab = tab;
    notifyDataSetChanged();
  }

  public int getCurrentTab() {
    return mTab;
  }

  @Override
  public int getCount() {
    switch (mTab) {
      case TAB_NEW:
        return 1 + mNewPosts.size();
      case TAB_FEATURE:
        return 1 + mFeaturePosts.size();
    }
    return 0;
  }

  @Override
  public Object getItem(int position) {
    if (position == 0) {
      return mTopic;
    } else {
      switch(mTab) {
        case TAB_NEW:
          return mNewPosts.get(position - 1);
        case TAB_FEATURE:
          return mFeaturePosts.get(position - 1);
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
    }
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return TYPE_POST;
    } else {
      return TYPE_TOPIC;
    }
  }

  private View getTopicView(int position, View convertView, ViewGroup parent) {
    TopicViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
      holder = new TopicViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (TopicViewHolder) convertView.getTag();
    }

    final Topic topic = (Topic) getItem(position);

    if (topic != null) {
      holder.mTvFeedCount.setText(topic.content);
      holder.mTvText.setText(topic.content);

      List<Tag> tags = topic.tags;
      for (int i = 0; i < tags.size(); i++) {
        Tag g = tags.get(i);
        switch (i) {
          case 0:
            holder.mTvTag1.setText(g.content);
            break;
          case 1:
            holder.mTvTag2.setText(g.content);
            break;
          case 2:
            holder.mTvTag3.setText(g.content);
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

    ((FeedPostView) convertView).setData((Post) getItem(position));

    return convertView;
  }

  static class TopicViewHolder {

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

    TopicViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}
