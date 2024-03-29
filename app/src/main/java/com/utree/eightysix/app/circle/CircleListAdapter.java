package com.utree.eightysix.app.circle;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.snapshot.SnapshotActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 */
class CircleListAdapter extends BaseAdapter {

  private static final int TYPE_CIRCLE = 0;
  private static final int TYPE_HEAD = 1;
  private List<Circle> mCircles;
  private SparseArray<String> mHeadMark;

  private int mMode;

  private ThemedDialog mCircleChangeDialog;

  public CircleListAdapter(List<Circle> circles, int mode) {
    mCircles = circles;
    mMode = mode;
    markHeadPosition();
  }

  public List<Circle> getCircles() {
    return mCircles == null ? new ArrayList<Circle>() : mCircles;
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
    switch (type) {
      case TYPE_CIRCLE:
        return getCircleView(position, convertView, parent);
      case TYPE_HEAD:
        return getHeadView(position, convertView, parent);
    }
    return convertView;
  }

  @Override
  public int getItemViewType(int position) {
    return mHeadMark.get(position) == null ? TYPE_CIRCLE : TYPE_HEAD;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  private View getHeadView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.item_head, null);
    }

    ((TextView) convertView.findViewById(R.id.tv_head)).setText(mHeadMark.get(position));

    TextView viewById = (TextView) convertView.findViewById(R.id.tv_right);
    if (mMode == BaseCirclesActivity.MODE_MY && position == 0) {
      viewById.setText(R.string.change);
      viewById.setVisibility(View.VISIBLE);
      viewById.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          showCircleChangeDialog(view.getContext());
        }
      });
    } else if (mMode == BaseCirclesActivity.MODE_SELECT && position == 0) {
      viewById.setText("找工作中，还没有在职工厂？");
      viewById.setVisibility(View.VISIBLE);
      viewById.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          BaseCirclesActivity.startSnapshot(view.getContext());
        }
      });
    } else {
      viewById.setVisibility(View.GONE);
    }
    return convertView;
  }

  private View getCircleView(int position, View convertView, ViewGroup parent) {
    CircleViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.item_circle, null);
      holder = new CircleViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (CircleViewHolder) convertView.getTag();
    }

    Circle item = getItem(position);

    holder.setCircle(item, parent.getResources());

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
    for (int i = 0; i < mCircles.size(); i++) {
      Circle circle = mCircles.get(i);
      if (circle == null) continue;

      if ((pre == null) || !circle.viewGroupType.equals(pre.viewGroupType)) {
        mHeadMark.put(i, circle.viewGroupType);
        mCircles.add(i, null);
      }
      pre = circle;
    }
  }

  protected void showCircleChangeDialog(Context context) {
    if (mCircleChangeDialog == null) {
      mCircleChangeDialog = new ThemedDialog(context);
      mCircleChangeDialog.setTitle("是否更改在职圈子？");
      TextView textView = new TextView(context);
      textView.setText(String.format("请注意：%d天之内不能修改哦",
          U.getSyncClient().getSync() != null ? U.getSyncClient().getSync().selectFactoryDays : 15));
      int p = U.dp2px(16);
      textView.setPadding(p, p, p, p);
      mCircleChangeDialog.setContent(textView);

      mCircleChangeDialog.setPositive("继续", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          BaseCirclesActivity.startSelect(v.getContext(), true);
          mCircleChangeDialog.dismiss();
        }
      });
      mCircleChangeDialog.setRbNegative("放弃", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mCircleChangeDialog.dismiss();
        }
      });
    }

    mCircleChangeDialog.show();
  }


  @Keep
  public class CircleViewHolder {

    private Circle mItem;

    @InjectView(R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView(R.id.tv_circle_info)
    public TextView mTvCircleInfo;

    @InjectView(R.id.rb_icon)
    public RoundedButton mRbIcon;

    @InjectView(R.id.rb_type)
    public RoundedButton mRbType;

    @InjectView(R.id.rb_snapshot)
    public TextView mRbSnapshot;

    @OnClick(R.id.rb_snapshot)
    public void onRbSnapshotClicked(View view) {
      SnapshotActivity.start(view.getContext(), mItem);
    }

    public void setCircle(Circle item, Resources res) {
      mItem = item;
      mTvCircleInfo.setText(item.info);

      if ("我所在的圈子".equals(item.viewGroupType)) {
        mTvCircleName.setText(item.shortName + "(在职)");
      } else {
        mTvCircleName.setText(item.shortName);
      }

      if (item.circleType == 1) {
        mRbIcon.setText(U.gs(R.string.factory));
        mRbIcon.setBackgroundColor(res.getColor(R.color.apptheme_primary_light_color));
      } else if (item.circleType == 2) {
        mRbIcon.setText(U.gs(R.string.business));
        mRbIcon.setBackgroundColor(0xffff6600);
      } else if (item.circleType == 3) {
        mRbIcon.setText("官");
        mRbIcon.setBackgroundColor(0xffff6600);
      }

      if (item.viewType == 0) {
        mRbType.setVisibility(View.GONE);
      } else {
        mRbType.setVisibility(View.VISIBLE);
        if (item.viewType == 1) {
          mRbType.setText(U.gs(R.string.most_friends));
          mRbType.setBackgroundColor(0xffea6161);
        } else if (item.viewType == 2) {
          mRbType.setText(U.gs(R.string.nearest));
          mRbType.setBackgroundColor(0xff1cbd51);
        } else if (item.viewType == 3) {
          mRbType.setText(U.gs(R.string.last_visited));
          mRbType.setBackgroundColor(0xff12bce7);
        }
      }

      if (item.snapshot == 0 || mMode == BaseCirclesActivity.MODE_SELECT) {
        mRbSnapshot.setVisibility(View.GONE);
      } else {
        mRbSnapshot.setVisibility(View.VISIBLE);
      }
    }

    public CircleViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}
