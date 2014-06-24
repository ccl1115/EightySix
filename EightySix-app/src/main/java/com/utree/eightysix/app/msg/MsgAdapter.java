package com.utree.eightysix.app.msg;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.utree.eightysix.data.Post;
import java.util.List;

/**
 * @author simon
 */
class MsgAdapter extends BaseAdapter {

  private List<Post> mPosts;

  public MsgAdapter(List<Post> posts) {
    mPosts = posts;
  }

  public void add(List<Post> posts) {
    if (mPosts == null) {
      mPosts = posts;
    } else {
      mPosts.addAll(posts);
    }
  }

  @Override
  public int getCount() {
    return mPosts == null ? 0 : (mPosts.size() + 1) >> 1;
  }

  @Override
  public Post[] getItem(int position) {
    if ((position << 1) + 1 >= mPosts.size()) {
      return new Post[] {mPosts.get(position << 1), null};
    } else {
      return new Post[] {mPosts.get(position << 1), mPosts.get((position << 1) + 1)};
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new MsgItemView(parent.getContext());
    }

    ((MsgItemView) convertView).setData(getItem(position));

    return convertView;
  }
}
