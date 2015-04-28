package com.utree.eightysix.app.topic;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.data.Topics;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;

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

    TextView textView = (TextView) convertView.findViewById(R.id.tv_head);
    textView.setCompoundDrawablePadding(U.dp2px(8));
    if (position == 0) {
      textView.setText(mTopics.newTopic.headTitle);
      textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_head_newest_topic, 0, 0, 0);
    } else {
      textView.setText(mTopics.hotTopic.headTitle);
      textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_head_daily_picks, 0, 0, 0);
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

    holder.setData((Topic) getItem(position));

    return convertView;
  }

  public static class TopicViewHolder {

    @InjectView(R.id.tv_tag_1)
    public TextView mTvTag1;

    @InjectView(R.id.tv_tag_2)
    public TextView mTvTag2;

    @InjectView(R.id.tv_text)
    public TextView mTvText;

    @InjectView(R.id.tv_title)
    public TextView mTvTitle;

    @InjectView(R.id.tv_more)
    public TextView mTvMore;

    @InjectView(R.id.ll_parent)
    public LinearLayout mLlParent;

    @InjectView(R.id.aiv_bg)
    public AsyncImageViewWithRoundCorner mAivBg;

    public void setData(Topic topic) {

      mTvMore.setText(String.format("%d条内容", topic.postCount));
      mTvText.setText(topic.content);
      if (TextUtils.isEmpty(topic.title)) {
        mTvTitle.setVisibility(View.GONE);
      } else {
        mTvTitle.setVisibility(View.VISIBLE);
      }
      mTvTitle.setText(topic.title);
      if (TextUtils.isEmpty(topic.bgUrl)) {
        mAivBg.setUrl(null);
        mLlParent.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4), ColorUtil.strToColor(topic.bgColor)));
      } else {
        mAivBg.setUrl(topic.bgUrl);
        mLlParent.setBackgroundDrawable(null);
      }

      mTvTag1.setText("");
      mTvTag2.setText("");

      List<Tag> tags = topic.tags;
      for (int i = 0; i < tags.size(); i++) {
        final Tag g = tags.get(i);
        switch (i) {
          case 0:
            mTvTag1.setText("#" + g.content);
            break;
          case 1:
            mTvTag2.setText("#" + g.content);
            break;
        }
      }
    }

    public TopicViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
