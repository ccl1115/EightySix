package com.utree.eightysix.app.feed;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.data.Circle;
import java.util.List;

/**
 * @author simon
 */
class SideCirclesAdapter extends BaseAdapter {

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
  public View getView(int position, View convertView, final ViewGroup parent) {
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

    Resources res = parent.getResources();
    if (circle.selected) {
      viewHolder.mLlItem.setBackgroundColor(res.getColor(R.color.apptheme_primary_light_color));
      viewHolder.mTvName.setTextColor(Color.WHITE);
    } else {
      viewHolder.mLlItem.setBackgroundDrawable(res.getDrawable(R.drawable.apptheme_primary_list_selector));
      viewHolder.mTvName.setTextColor(res.getColor(R.color.apptheme_primary_text_dark));
    }
    return convertView;
  }

  @Keep
  static class ViewHolder {

    @InjectView (R.id.name)
    public TextView mTvName;

    @InjectView (R.id.friends)
    public TextView mTvFriends;

    @InjectView (R.id.ll_item)
    public LinearLayout mLlItem;

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
