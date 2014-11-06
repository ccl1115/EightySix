package com.utree.eightysix.app.region;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.data.Circle;

import java.util.List;

/**
 * @author simon
 */
class FactoryRegionAdapter extends BaseAdapter {

  private List<Circle> mCircles;

  public FactoryRegionAdapter(List<Circle> circles) {
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

  public void set(List<Circle> circles) {
    mCircles = circles;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mCircles == null ? 0 : Math.min(10, mCircles.size());
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

    viewHolder.mTvName.setText(circle.shortName);
    viewHolder.mTvFriends.setText(circle.info);

    Resources res = parent.getResources();
    if (circle.currFactory == 1) {
      viewHolder.mIvHouse.setVisibility(View.VISIBLE);
    } else {
      viewHolder.mIvHouse.setVisibility(View.INVISIBLE);
    }

    switch (circle.hotLevel) {
      case 0:
        viewHolder.mIvFire.setVisibility(View.GONE);
        break;
      case 1:
        viewHolder.mIvFire.setImageResource(R.drawable.fire_1);
        viewHolder.mIvFire.setVisibility(View.VISIBLE);
        break;
      case 2:
        viewHolder.mIvFire.setImageResource(R.drawable.fire_2);
        viewHolder.mIvFire.setVisibility(View.VISIBLE);
        break;
      case 3:
        viewHolder.mIvFire.setImageResource(R.drawable.fire_3);
        viewHolder.mIvFire.setVisibility(View.VISIBLE);
        break;
      case 4:
        viewHolder.mIvFire.setImageResource(R.drawable.fire_4);
        viewHolder.mIvFire.setVisibility(View.VISIBLE);
        break;
      case 5:
        viewHolder.mIvFire.setImageResource(R.drawable.fire_5);
        viewHolder.mIvFire.setVisibility(View.VISIBLE);
        break;
    }

      viewHolder.mLlItem.setBackgroundDrawable(res.getDrawable(R.drawable.apptheme_primary_list_selector));
    viewHolder.mTvName.setTextColor(res.getColorStateList(R.color.apptheme_primary_text_dark));
      viewHolder.mTvFriends.setTextColor(0xffb3b3b3);
      viewHolder.mIvHouse.setImageResource(R.drawable.house);

    if (circle.lock == 1) {
      viewHolder.mTvFriends.setCompoundDrawablesWithIntrinsicBounds(
          res.getDrawable(R.drawable.ic_lock_small), null, null, null);
      viewHolder.mTvFriends.setCompoundDrawablePadding(U.dp2px(5));
    } else {
      viewHolder.mTvFriends.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    return convertView;
  }

  @Keep
  static class ViewHolder {

    @InjectView(R.id.name)
    public TextView mTvName;

    @InjectView(R.id.friends)
    public TextView mTvFriends;

    @InjectView(R.id.ll_item)
    public LinearLayout mLlItem;

    @InjectView(R.id.iv_house)
    public ImageView mIvHouse;

    @InjectView(R.id.iv_fire)
    public ImageView mIvFire;

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
