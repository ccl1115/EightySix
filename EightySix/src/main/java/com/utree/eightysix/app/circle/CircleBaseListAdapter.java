package com.utree.eightysix.app.circle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.response.data.Circle;
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

    final String info =
        String.format("%.1fkm | 朋友(%d) | 工友(%d)", item.distance / 1000f, item.friendCount, item.workmateCount);

    viewHolder.mTvCircleInfo.setText(info);
    viewHolder.mTvCircleName.setText(item.name);

    return convertView;
  }

  public static class CircleBaseViewHolder {

    @InjectView (R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView (R.id.tv_circle_info)
    public TextView mTvCircleInfo;
  }
}
