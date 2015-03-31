/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 */
public class FriendContactListAdapter extends BaseAdapter {

  private List<Friend> mFriends;

  public FriendContactListAdapter(List<Friend> friends) {
    mFriends = friends;
  }

  @Override
  public int getCount() {
    return mFriends.size();
  }

  @Override
  public Friend getItem(int position) {
    return mFriends.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {

    } else {

    }


    return convertView;
  }

  public static class ViewHolder {

    @InjectView(R.id.tv_name)
    public TextView mTvName;

    @InjectView(R.id.tv_source)
    public TextView mTvSource;

    @InjectView(R.id.rb_add)
    public RoundedButton mRbAdd;

    @InjectView(R.id.tv_result)
    public TextView mTvResult;
    private Friend mFriend;

    @OnClick(R.id.rb_add)
    public void onRbAddClicked(View v) {
      SendRequestActivity.start(v.getContext(), mFriend.viewId);
    }

    public void setData(Friend friend) {
      mFriend = friend;

      mTvName.setText(mFriend.name);
      mTvSource.setText(mFriend.workinFactory);

      if ("added".equals(mFriend.type)) {
        mTvResult.setText("请求已发送");
        mTvResult.setVisibility(View.VISIBLE);
        mRbAdd.setVisibility(View.GONE);
      } else if ("passed".equals(mFriend.type)) {
        mTvResult.setText("已添加");
        mTvResult.setVisibility(View.VISIBLE);
        mRbAdd.setVisibility(View.GONE);
      } else if ("ignored".equals(mFriend.type)) {
        mTvResult.setText("被忽略");
        mTvResult.setVisibility(View.VISIBLE);
        mRbAdd.setVisibility(View.GONE);
      } else {
        mTvResult.setVisibility(View.GONE);
        mRbAdd.setVisibility(View.VISIBLE);
      }
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
