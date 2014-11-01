package com.utree.eightysix.app.tag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.publish.MoreTagsItemLayout;
import com.utree.eightysix.app.publish.TagsLayout;
import com.utree.eightysix.response.TagsByTypeResponse;

/**
 */
public class MoreTagAdapter extends BaseAdapter {

  private TagsByTypeResponse.TagsByType mTags;

  public MoreTagAdapter(TagsByTypeResponse.TagsByType tags) {
    mTags = tags;
  }

  @Override
  public int getCount() {
    return mTags.lists.size();
  }

  @Override
  public Object getItem(int position) {
    return mTags.lists.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
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
