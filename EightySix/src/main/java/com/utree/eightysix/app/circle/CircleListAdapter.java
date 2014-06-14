package com.utree.eightysix.app.circle;

import android.util.SparseArray;
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
import java.util.Iterator;
import java.util.List;

/**
 */
class CircleListAdapter extends BaseAdapter {

  private static final int TYPE_CIRCLE = 1;
  private static final int TYPE_HEAD = 2;

  private List<Circle> mCircles;
  private SparseArray<String> mHeadMark;


  public CircleListAdapter(List<Circle> circles) {
    mCircles = circles;
    markHeadPosition();
  }

  public void add(Collection<Circle> collection) {
    mCircles.addAll(collection);
    markHeadPosition();
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
    final int type = getItemViewType(position);
    if (type == TYPE_CIRCLE) {
      return getCircleView(position, convertView, parent);
    } else if (type == TYPE_HEAD) {
      if (convertView == null) {
        convertView = View.inflate(parent.getContext(), R.layout.item_head_circle, null);
      }

      ((TextView) convertView.findViewById(R.id.tv_circle_group_type)).setText(mHeadMark.get(position));
      return convertView;
    }
    return convertView;
  }

  @Override
  public int getItemViewType(int position) {
    return mHeadMark.get(position) == null ? TYPE_CIRCLE : TYPE_HEAD;
  }

  @Override
  public int getViewTypeCount() {
    return mHeadMark.size() + 1;
  }

  private View getCircleView(int position, View convertView, ViewGroup parent) {
    CircleViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.item_circle, null);
      holder = new CircleViewHolder();
      ButterKnife.inject(holder, convertView);
      convertView.setTag(holder);
    } else {
      holder = (CircleViewHolder) convertView.getTag();
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

  private void markHeadPosition() {
    mHeadMark = new SparseArray<String>();
    for (Iterator<Circle> iterator = mCircles.iterator(); iterator.hasNext(); ) {
      Circle circle = iterator.next();
      if (circle == null) {
        iterator.remove();
      }
    }
    Circle pre = null;
    for (int i = 0, size = mCircles.size(); i < size; i++) {
      Circle circle = mCircles.get(i);
      if (circle == null) continue;

      if ((pre == null) || !circle.viewGroupType.equals(pre.viewGroupType)) {
        mHeadMark.put(i, circle.viewGroupType);
        mCircles.add(i, null);
      }
      pre = circle;
    }
  }

  public static class CircleViewHolder {
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
