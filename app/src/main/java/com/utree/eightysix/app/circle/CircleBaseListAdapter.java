package com.utree.eightysix.app.circle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.snapshot.SnapshotActivity;
import com.utree.eightysix.data.Circle;

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
      viewHolder = new CircleBaseViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (CircleBaseViewHolder) convertView.getTag();
    }

    viewHolder.setCircle(getItem(position));

    return convertView;
  }

  @Keep
  public static class CircleBaseViewHolder {

    private Circle mCircle;

    public CircleBaseViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void setCircle(Circle circle) {
      mCircle = circle;
      mTvCircleInfo.setText(circle.info);
      mTvCircleName.setText(circle.name);

      if (circle.snapshot == 1) {
        mRbSnapshot.setVisibility(View.VISIBLE);
      } else {
        mRbSnapshot.setVisibility(View.INVISIBLE);
      }
    }

    @InjectView (R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView (R.id.tv_circle_info)
    public TextView mTvCircleInfo;

    @InjectView(R.id.rb_snapshot)
    public TextView mRbSnapshot;

    @OnClick(R.id.rb_snapshot)
    public void onRbSnapshotClicked(View view) {
      SnapshotActivity.start(view.getContext(), mCircle);
    }
  }
}
