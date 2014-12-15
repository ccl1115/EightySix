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
import com.utree.eightysix.app.tag.TagTabActivity;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.data.Topics;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ColorUtil;
import java.util.List;

/**
 */
public class TopicListAdapter extends BaseAdapter {

  private static final int TYPE_COUNT = 2;

  private static final int TYPE_HEAD = 0;
  private static final int TYPE_TOPIC = 1;


  private Topics mTopics;

  public TopicListAdapter(Topics topics) {
    mTopics = topics;
  }

  public void add(List<Topic> topicList) {
    mTopics.hotTopic.postTopics.lists.addAll(topicList);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return 2 + mTopics.hotTopic.postTopics.lists.size() + mTopics.newTopic.postTopics.lists.size();
  }

  @Override
  public Object getItem(int i) {
    int newTopicSize = 1 + mTopics.newTopic.postTopics.lists.size();
    if (i == 0) {
      return null;
    } else if (i > 0 && i < newTopicSize) {
      return mTopics.newTopic.postTopics.lists.get(i - 1);
    } else if (i == newTopicSize) {
      return null;
    } else if (i > newTopicSize) {
      return mTopics.hotTopic.postTopics.lists.get(i - 1 - newTopicSize);
    }
    return null;
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_HEAD:
        return getHeadView(position, convertView, parent);
      case TYPE_TOPIC:
        return getTopicView(position, convertView, parent);
    }
    return null;
  }

  @Override
  public int getItemViewType(int i) {
    int newTopicSize = 1 + mTopics.newTopic.postTopics.lists.size();
    if (i == 0) {
      return TYPE_HEAD;
    } else if (i > 0 && i < newTopicSize) {
      return TYPE_TOPIC;
    } else if (i == newTopicSize) {
      return TYPE_HEAD;
    } else if (i > newTopicSize) {
      return TYPE_TOPIC;
    }
    return TYPE_HEAD;
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }

  private View getHeadView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head, parent, false);
    }

    if (position == 0) {
      ((TextView) convertView.findViewById(R.id.tv_head)).setText(mTopics.newTopic.headTitle);
    } else {
      ((TextView) convertView.findViewById(R.id.tv_head)).setText(mTopics.hotTopic.headTitle);
    }

    return convertView;
  }

  private View getTopicView(int position, View convertView, ViewGroup parent) {
    TopicViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_topic, parent, false);
      holder = new TopicViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = ((TopicViewHolder) convertView.getTag());
    }

    Topic topic = ((Topic) getItem(position));

    holder.mTvMore.setText(String.format("%d条内容", topic.postCount));
    holder.mTvText.setText(topic.content);
    holder.mLlParent.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4), ColorUtil.strToColor(topic.bgColor)));

    holder.mTvTag1.setText("");
    holder.mTvTag2.setText("");
    holder.mTvTag3.setText("");

    List<Tag> tags = topic.tags;
    for (int i = 0; i < tags.size(); i++) {
      final Tag g = tags.get(i);
      switch (i) {
        case 0:
          holder.mTvTag1.setText("#" + g.content);
          break;
        case 1:
          holder.mTvTag2.setText("#" + g.content);
          break;
        case 2:
          holder.mTvTag3.setText("#" + g.content);
          break;
      }
    }

    return convertView;
  }

  public static class TopicViewHolder {

    @InjectView (R.id.tv_tag_1)
    public TextView mTvTag1;

    @InjectView (R.id.tv_tag_2)
    public TextView mTvTag2;

    @InjectView (R.id.tv_tag_3)
    public TextView mTvTag3;

    @InjectView (R.id.tv_text)
    public TextView mTvText;

    @InjectView (R.id.tv_more)
    public TextView mTvMore;

    @InjectView (R.id.ll_parent)
    public LinearLayout mLlParent;

    public TopicViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
