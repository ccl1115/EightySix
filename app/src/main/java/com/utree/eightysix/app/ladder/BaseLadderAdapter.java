/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.ladder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.data.RankedUser;
import com.utree.eightysix.response.BaseLadderResponse;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;

import java.util.List;

/**
 */
public class BaseLadderAdapter extends BaseAdapter {

  private static final int TYPE_COUNT = 2;
  private static final int TYPE_BANNER = 0;
  private static final int TYPE_ITEM = 1;
  private final BaseLadderResponse.Extra mExtra;

  private List<RankedUser> mUsers;


  public BaseLadderAdapter(BaseLadderResponse response) {
    mUsers = response.object;
    mExtra = response.extra;
  }

  @Override
  public int getCount() {
    return mUsers.size();
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? TYPE_BANNER : TYPE_ITEM;
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }

  @Override
  public Object getItem(int position) {
    return position == 0 ? mExtra : mUsers.get(position - 1);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_BANNER:
        return getBannerView(convertView, parent);
      case TYPE_ITEM:
        return getItemView(position, convertView, parent);
    }
    return null;
  }

  private View getBannerView(View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new LadderBannerView(parent.getContext());
    }
    ((LadderBannerView) convertView).setData(mExtra);
    return convertView;
  }

  private View getItemView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranked_user, parent ,false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    holder.setData((RankedUser) getItem(position));
    return convertView;
  }

  public static class ViewHolder {

    @InjectView(R.id.tv_name)
    public TextView mTvName;

    @InjectView(R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView(R.id.tv_rank)
    public TextView mTvRank;

    @InjectView(R.id.tv_exp)
    public TextView mTvExp;

    @InjectView(R.id.aiv_portrait)
    public AsyncImageViewWithRoundCorner mAivPortrait;

    @InjectView(R.id.aiv_level_icon)
    public AsyncImageView mAivLevelIcon;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void setData(RankedUser user) {
      mTvName.setText(user.userName);
      mTvCircleName.setText(user.workinFactory);
      mTvRank.setText(String.valueOf(user.rank));
      mTvExp.setText("+" + user.experience);
      mAivLevelIcon.setUrl(user.levelIcon);
      mAivPortrait.setUrl(user.avatar);
    }
  }
}
