package com.utree.eightysix.app.circle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.response.data.Circle;
import com.utree.eightysix.widget.RoundedButton;
import java.util.Collection;
import java.util.List;

/**
 */
public class CircleListAdapter extends BaseAdapter {

  private List<Circle> mCircles;

  public CircleListAdapter(List<Circle> circles) {
    mCircles = circles;
  }

  public void add(Collection<Circle> collection) {
    mCircles.addAll(collection);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mCircles == null ? 0 : mCircles.size();
  }

  @Override
  public Circle getItem(int position) {
    return mCircles.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.item_circle, null);
      holder = new ViewHolder();
      ButterKnife.inject(holder, convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    Circle item = getItem(position);
    final String info = String.format("%.1fkm | 朋友(%d) | 工友(%d)", item.distance / 1000f, item.friendCount, item.workmateCount);
    holder.mTvCircleInfo.setText(info);
    holder.mTvCircleName.setText(item.name);
    if (item.circleType == 1) {
      holder.mRbIcon.setText("工");
      holder.mRbIcon.setBackgroundColor(0xff6a51a5);
    } else {
      holder.mRbIcon.setText("商");
      holder.mRbIcon.setBackgroundColor(0xffff6600);
    }

    if (item.viewType == 4) {
      holder.mRbType.setVisibility(View.INVISIBLE);
    } else {
      holder.mRbType.setVisibility(View.VISIBLE);
      if (item.viewType == 1) {
        holder.mRbType.setText("朋友最多");
        holder.mRbType.setBackgroundColor(0xffea6161);
      } else if (item.viewType == 2) {
        holder.mRbType.setText("距离最近");
        holder.mRbType.setBackgroundColor(0xff1cbd51);
      } else if (item.viewType == 3) {
        holder.mRbType.setText("最后访问");
        holder.mRbType.setBackgroundColor(0xff12bce7);
      }
    }

    return convertView;
  }

  public static class ViewHolder {
    @InjectView (R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView (R.id.tv_circle_info)
    public TextView mTvCircleInfo;

    @InjectView (R.id.rb_icon)
    public RoundedButton mRbIcon;

    @InjectView (R.id.rb_type)
    public RoundedButton mRbType;

    @InjectView (R.id.iv_arrow)
    public ImageView mIvArrow;
  }

}
