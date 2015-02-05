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
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.snapshot.SnapshotActivity;
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

    viewHolder.setData(circle, parent.getResources());

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

    @InjectView(R.id.rb_snapshot)
    public TextView mRbSnapshot;
    private Circle mCircle;

    @OnClick(R.id.rb_snapshot)
    public void onRbSnapshotClicked(View v) {
      SnapshotActivity.start(v.getContext(), mCircle);
    }

    public void setData(Circle circle, Resources res) {
      mCircle = circle;
      mTvName.setText(circle.shortName);
      mTvInfo.setText(circle.info);

      if (circle.currFactory == 1) {
        mIvHouse.setVisibility(View.VISIBLE);
      } else {
        mIvHouse.setVisibility(View.INVISIBLE);
      }

      switch (circle.hotLevel) {
        case 0:
          mIvFire.setVisibility(View.GONE);
          break;
        case 1:
          mIvFire.setImageResource(R.drawable.fire_1);
          mIvFire.setVisibility(View.VISIBLE);
          break;
        case 2:
          mIvFire.setImageResource(R.drawable.fire_2);
          mIvFire.setVisibility(View.VISIBLE);
          break;
        case 3:
          mIvFire.setImageResource(R.drawable.fire_3);
          mIvFire.setVisibility(View.VISIBLE);
          break;
        case 4:
          mIvFire.setImageResource(R.drawable.fire_4);
          mIvFire.setVisibility(View.VISIBLE);
          break;
        case 5:
          mIvFire.setImageResource(R.drawable.fire_5);
          mIvFire.setVisibility(View.VISIBLE);
          break;
      }

      mTvName.setTextColor(res.getColorStateList(R.color.apptheme_primary_text_dark));
      mTvInfo.setTextColor(0xffb3b3b3);
      mIvHouse.setImageResource(R.drawable.house);

      if (circle.lock == 1) {
        mTvInfo.setCompoundDrawablesWithIntrinsicBounds(
            res.getDrawable(R.drawable.ic_lock_small), null, null, null);
        mTvInfo.setCompoundDrawablePadding(U.dp2px(5));
      } else {
        mTvInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
      }

      if (circle.snapshot == 1) {
        mRbSnapshot.setVisibility(View.VISIBLE);
      } else {
        mRbSnapshot.setVisibility(View.GONE);
      }
    }

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
