/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.circle;

import android.content.res.Resources;
import android.view.LayoutInflater;
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
import com.utree.eightysix.data.FollowCircle;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 */
public class FollowCircleListAdapter extends BaseAdapter {

  private List<FollowCircle> mCircles;

  private boolean mShowDelete;

  public FollowCircleListAdapter(List<FollowCircle> circles) {

    mCircles = circles;
  }

  @Override
  public int getCount() {
    return mCircles.size();
  }

  @Override
  public FollowCircle getItem(int position) {
    return mCircles.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_circle, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(convertView);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.setData(getItem(position), parent.getResources());

    return convertView;
  }

  public void toggleDelete() {
    mShowDelete = !mShowDelete;
    notifyDataSetChanged();
  }

  public void remove(FollowCircle item) {
    mCircles.remove(item);
    notifyDataSetChanged();
  }

  @Keep
  public class ViewHolder {

    private FollowCircle mItem;

    @InjectView(R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView(R.id.tv_circle_info)
    public TextView mTvCircleInfo;

    @InjectView(R.id.rb_icon)
    public RoundedButton mRbIcon;

    @InjectView(R.id.tv_delete)
    public TextView mTvDelete;

    @OnClick(R.id.tv_delete)
    public void onTvDeleteClicked(View view) {
      U.request("follow_circle_del", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {

        }

        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            mCircles.remove(mItem);
            notifyDataSetChanged();
          }

        }
      }, Response.class, mItem.factoryId);
    }

    public void setData(FollowCircle item, Resources res) {
      mItem = item;
      mTvCircleInfo.setText(String.format("朋友(%d) | 工友(%d)", item.friendCount, item.workerCount));

      mTvCircleName.setText(item.factoryName);

      if (item.factoryType == 1) {
        mRbIcon.setText(U.gs(R.string.factory));
        mRbIcon.setBackgroundColor(res.getColor(R.color.apptheme_secondary_dark_color));
      } else if (item.factoryType == 2) {
        mRbIcon.setText(U.gs(R.string.business));
        mRbIcon.setBackgroundColor(0xffff6600);
      } else if (item.factoryType == 3) {
        mRbIcon.setText("官");
        mRbIcon.setBackgroundColor(0xffff6600);
      }

      mTvDelete.setVisibility(mShowDelete ? View.VISIBLE : View.GONE);
    }


    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
