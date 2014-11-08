package com.utree.eightysix.app.circle;

import android.content.Context;
import android.util.SparseArray;
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

  private ThemedDialog mCircleChangeDialog;

  public CircleListAdapter(List<Circle> circles) {
    mCircles = circles;
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

    holder.mTvCircleInfo.setText(item.info);

    if (item.currFactory == 1) {
      holder.mRbChange.setVisibility(View.VISIBLE);
    } else {
      holder.mRbChange.setVisibility(View.INVISIBLE);
    }


    if ("我所在的圈子".equals(item.viewGroupType)) {
      holder.mTvCircleName.setText(item.shortName + "(在职)");
    } else {
      holder.mTvCircleName.setText(item.shortName);
    }

    if (item.circleType == 1) {
      holder.mRbIcon.setText(U.gs(R.string.factory));
      holder.mRbIcon.setBackgroundColor(0xff6a51a5);
    } else {
      holder.mRbIcon.setText(U.gs(R.string.business));
      holder.mRbIcon.setBackgroundColor(0xffff6600);
    }

    if (item.viewType == 0) {
      holder.mRbType.setVisibility(View.INVISIBLE);
    } else {
      holder.mRbType.setVisibility(View.VISIBLE);
      if (item.viewType == 1) {
        holder.mRbType.setText(U.gs(R.string.most_friends));
        holder.mRbType.setBackgroundColor(0xffea6161);
      } else if (item.viewType == 2) {
        holder.mRbType.setText(U.gs(R.string.nearest));
        holder.mRbType.setBackgroundColor(0xff1cbd51);
      } else if (item.viewType == 3) {
        holder.mRbType.setText(U.gs(R.string.last_visited));
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
      mCircleChangeDialog.setTitle("确认更改在职圈子");
      TextView textView = new TextView(context);
      textView.setText(String.format("请注意：%d天之内不能修改哦",
          U.getSyncClient().getSync() != null ? U.getSyncClient().getSync().selectFactoryDays : 15));
      int p = U.dp2px(16);
      textView.setPadding(p, p, p, p);
      mCircleChangeDialog.setContent(textView);

      mCircleChangeDialog.setPositive("继续", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          BaseCirclesActivity.startSelect(v.getContext());
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

    @InjectView(R.id.rb_change)
    public RoundedButton mRbChange;

    @OnClick(R.id.rb_change)
    public void onRbChangeClicked(View v) {
      showCircleChangeDialog(v.getContext());
    }

    public CircleViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}
