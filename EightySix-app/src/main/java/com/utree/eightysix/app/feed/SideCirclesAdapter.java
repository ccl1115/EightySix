package com.utree.eightysix.app.feed;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.circle.MyCirclesActivity;
import com.utree.eightysix.data.Circle;
import java.util.List;

/**
 * @author simon
 */
class SideCirclesAdapter extends BaseAdapter {

  private static final int TYPE_CIRCLE = 0;
  private static final int TYPE_MORE = 1;

  private List<Circle> mCircles;

  public SideCirclesAdapter(List<Circle> circles) {
    mCircles = circles;
  }

  public void add(List<Circle> circles) {
    if (mCircles == null) {
      mCircles = circles;
    } else {
      mCircles.addAll(circles);
    }
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mCircles == null ? 0 : mCircles.size() + 1;
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
  public View getView(int position, View convertView, final ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_CIRCLE:
        convertView = getCircleView(position, convertView, parent);
        break;
      case TYPE_MORE:
        if (convertView == null) {
          convertView = View.inflate(parent.getContext(), R.layout.item_side_more, null);
        }

        convertView.findViewById(R.id.rb_more).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            parent.getContext().startActivity(new Intent(parent.getContext(), MyCirclesActivity.class));
          }
        });
    }

    return convertView;
  }

  private View getCircleView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_side_circle, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Circle circle = getItem(position);

    viewHolder.mTvName.setText(circle.name);
    viewHolder.mTvFriends.setText(
        String.format(parent.getContext().getString(R.string.friends_info),
            circle.friendCount, circle.workmateCount));
    return convertView;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    return (position == getCount() - 1) ? TYPE_MORE : TYPE_CIRCLE;
  }

  @Keep
  static class ViewHolder {

    @InjectView (R.id.name)
    public TextView mTvName;

    @InjectView (R.id.friends)
    public TextView mTvFriends;

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
