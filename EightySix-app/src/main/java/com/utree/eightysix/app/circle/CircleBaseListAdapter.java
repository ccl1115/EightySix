package com.utree.eightysix.app.circle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.utils.*;
import java.util.Collection;
import java.util.List;

/**
 * @author simon
 */
class CircleBaseListAdapter extends BaseAdapter {

  private List<Circle> mBaseCircles;

  public CircleBaseListAdapter(List<Circle> circles) {
    mBaseCircles = circles;
  }

  public void add(Collection<Circle> list) {
    mBaseCircles.addAll(list);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mBaseCircles == null ? 0 : mBaseCircles.size();
  }

  @Override
  public Circle getItem(int position) {
    return mBaseCircles.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    CircleBaseViewHolder viewHolder;

    if (convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.item_circle_base, null);
      viewHolder = new CircleBaseViewHolder();
      ButterKnife.inject(viewHolder, convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (CircleBaseViewHolder) convertView.getTag();
    }

    Circle item = getItem(position);


    final String info;
    final String distance = com.utree.eightysix.utils.Utils.getDisplayDistance(item.distance);
    if (item.distance < 100) {
      info = String.format("%s | 朋友(%d) | 工友(%d)", distance, item.friendCount, item.workmateCount);
    } else if (item.distance < 1000) {
      info = String.format("%s | 朋友(%d) | 工友(%d)", distance, item.friendCount, item.workmateCount);
    } else {
      info = String.format("%s | 朋友(%d) | 工友(%d)", distance, item.friendCount, item.workmateCount);
    }

    viewHolder.mTvCircleInfo.setText(info);
    viewHolder.mTvCircleName.setText(item.name);

    return convertView;
  }

  @Keep
  public static class CircleBaseViewHolder {

    @InjectView (R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView (R.id.tv_circle_info)
    public TextView mTvCircleInfo;
  }
}
