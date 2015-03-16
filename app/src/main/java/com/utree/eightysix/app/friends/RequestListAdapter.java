/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.data.FriendRequest;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 */
public class RequestListAdapter extends BaseAdapter {

  private List<FriendRequest> mRequest;

  public RequestListAdapter(@NonNull List<FriendRequest> requests) {
    mRequest = requests;
  }

  @Override
  public int getCount() {
    return mRequest.size();
  }

  @Override
  public FriendRequest getItem(int position) {
    return mRequest.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    RequestViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
      holder = new RequestViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (RequestViewHolder) convertView.getTag();
    }

    holder.setData(getItem(position));
    return convertView;
  }

  @Keep
  public static class RequestViewHolder {

    @InjectView(R.id.tv_name)
    public TextView mTvName;

    @InjectView(R.id.tv_info)
    public TextView mTvInfo;

    @InjectView(R.id.tv_timestamp)
    public TextView mTvTimestamp;

    @InjectView(R.id.tv_result)
    public TextView mTvResult;

    @InjectView(R.id.rb_accept)
    public RoundedButton mRbAccept;

    @InjectView(R.id.tv_ignore)
    public TextView mTvIgnore;


    private FriendRequest mRequest;

    @OnClick(R.id.rb_accept)
    public void onRbAcceptClicked() {

    }

    @OnClick(R.id.tv_ignore)
    public void onTvIgnoreClicked() {

    }

    public void setData(FriendRequest request) {
      mRequest = request;

      mTvName.setText(mRequest.userName);
      mTvInfo.setText(mRequest.content);
      mTvTimestamp.setText(TimeUtil.getElapsed(mRequest.timestamp));
      if ("added".equals(mRequest.type)) {
        mRbAccept.setVisibility(View.VISIBLE);
        mTvIgnore.setVisibility(View.VISIBLE);
        mTvResult.setVisibility(View.INVISIBLE);
      } else if ("ignored".equals(mRequest.type)) {
        mRbAccept.setVisibility(View.INVISIBLE);
        mTvIgnore.setVisibility(View.INVISIBLE);
        mTvResult.setText("已同意");
        mTvResult.setText("已忽略");
      } else if ("passed".equals(mRequest.type)) {
        mTvResult.setVisibility(View.VISIBLE);
        mRbAccept.setVisibility(View.INVISIBLE);
        mTvIgnore.setVisibility(View.INVISIBLE);
        mTvResult.setText("已同意");
      }
    }

    public RequestViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
