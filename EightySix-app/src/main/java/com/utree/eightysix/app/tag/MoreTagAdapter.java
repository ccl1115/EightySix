package com.utree.eightysix.app.tag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.publish.MoreTagsItemLayout;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.app.topic.TopicListAdapter;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.response.TagsByTypeResponse;
import com.utree.eightysix.utils.ColorUtil;
import java.util.List;

/**
 */
public class MoreTagAdapter extends BaseAdapter {

  private static final int TYPE_COUNT = 3;

  private static final int TYPE_HEAD = 0;
  private static final int TYPE_TOPIC = 1;
  private static final int TYPE_TAGS = 2;

  private TagsByTypeResponse.TagsByType mTags;


  public MoreTagAdapter(TagsByTypeResponse.TagsByType tags) {
    mTags = tags;
  }

  @Override
  public int getCount() {
    return mTags.lists.size() + 3;
  }

  @Override
  public Object getItem(int position) {
    if (position == 0 || position == 2) {
      return null;
    } else if (position == 1) {
      return mTags.topic;
    } else {
      return mTags.lists.get(position - 3);
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_TAGS:
        return getTagsView(position, convertView, parent);
      case TYPE_HEAD:
        return getHeadView(position, convertView, parent);
      case TYPE_TOPIC:
        return getTopicView(convertView, parent);

    }

    return null;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0 || position == 2) {
      return TYPE_HEAD;
    } else if (position == 1) {
      return TYPE_TOPIC;
    } else {
      return TYPE_TAGS;
    }
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }


  private View getTopicView(View convertView, ViewGroup parent) {
    TopicListAdapter.TopicViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_topic, parent, false);
      holder = new TopicListAdapter.TopicViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (TopicListAdapter.TopicViewHolder) convertView.getTag();
    }

    holder.mTvMore.setText(String.format("%d条内容", mTags.topic.postCount));
    holder.mTvText.setText(mTags.topic.content);
    holder.mLlParent.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4), ColorUtil.strToColor(mTags.topic.bgColor)));

    holder.mTvTag1.setText("");
    holder.mTvTag2.setText("");
    holder.mTvTag3.setText("");

    List<Tag> tags = mTags.topic.tags;
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

  private View getHeadView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head, parent, false);
    }

    TextView textView = (TextView) convertView.findViewById(R.id.tv_head);
    if (position == 0) {
      textView.setText("本期话题");

      TextView right = (TextView) convertView.findViewById(R.id.tv_right);
      right.setText("全部话题 》");
      right.setVisibility(View.VISIBLE);
      right.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          TopicListActivity.start(v.getContext());
        }
      });
    } else if (position == 2) {
      textView.setText("热门标签");
    }

    return convertView;
  }

  private View getTagsView(int position, View convertView, ViewGroup parent) {
    TypedTagsViewHolder holder;

    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_typed_tags, parent, false);
      holder = new TypedTagsViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (TypedTagsViewHolder) convertView.getTag();
    }

    TagsByTypeResponse.TypedTags tags = (TagsByTypeResponse.TypedTags) getItem(position);

    holder.mTvHead.setText(tags.typeName);
    holder.mTlTags.setTag(tags.tags);

    return convertView;
  }

  static class TypedTagsViewHolder {

    @InjectView (R.id.tv_head)
    public TextView mTvHead;

    @InjectView (R.id.tl_tags)
    public MoreTagsItemLayout mTlTags;

    public TypedTagsViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}
