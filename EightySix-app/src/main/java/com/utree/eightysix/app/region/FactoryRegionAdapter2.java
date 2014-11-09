package com.utree.eightysix.app.region;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
class FactoryRegionAdapter2 extends BaseAdapter {

  private List<Circle> mCircles;

  public FactoryRegionAdapter2(List<Circle> circles) {
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
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_region, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Circle circle = getItem(position);

    viewHolder.mTvName.setText(circle.shortName);
    viewHolder.mTvInfo.setText(circle.info);

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

    viewHolder.mTvName.setTextColor(res.getColorStateList(R.color.apptheme_primary_text_dark));
      viewHolder.mTvInfo.setTextColor(0xffb3b3b3);
      viewHolder.mIvHouse.setImageResource(R.drawable.house);

    if (circle.lock == 1) {
      viewHolder.mTvInfo.setCompoundDrawablesWithIntrinsicBounds(
          res.getDrawable(R.drawable.ic_lock_small), null, null, null);
      viewHolder.mTvInfo.setCompoundDrawablePadding(U.dp2px(5));
    } else {
      viewHolder.mTvInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    return convertView;
  }

  @Keep
  static class ViewHolder {

    @InjectView(R.id.tv_circle_name)
    public TextView mTvName;

    @InjectView(R.id.tv_circle_info)
    public TextView mTvInfo;

    @InjectView(R.id.iv_house)
    public ImageView mIvHouse;

    @InjectView(R.id.iv_fire)
    public ImageView mIvFire;

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
