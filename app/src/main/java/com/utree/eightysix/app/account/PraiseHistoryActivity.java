/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.data.PraisedUser;
import com.utree.eightysix.response.PraisedUsersResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.List;

/**
 */
@Layout(R.layout.activity_praise_history)
public class PraiseHistoryActivity extends BaseActivity {

  @InjectView(R.id.alv_praised_users)
  public AdvancedListView mAdvPraisedUsers;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  public static void start(Context context, int viewId) {
    Intent intent = new Intent(context, PraiseHistoryActivity.class);

    intent.putExtra("viewId", viewId);

    if (!(context instanceof Activity)) {
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    U.request("praised_users", new OnResponse2<PraisedUsersResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(PraisedUsersResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object == null || response.object.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
            mAdvPraisedUsers.setAdapter(null);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mAdvPraisedUsers.setAdapter(new PraiseHistoryAdapter(response.object));
          }
        }
      }
    }, PraisedUsersResponse.class);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  public static class PraiseHistoryAdapter extends BaseAdapter {

    private List<PraisedUser> mPraisedUserList;

    public PraiseHistoryAdapter(List<PraisedUser> users) {
      mPraisedUserList = users;
    }

    @Override
    public int getCount() {
      return mPraisedUserList.size();
    }

    @Override
    public PraisedUser getItem(int position) {
      return mPraisedUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if (convertView == null) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_praised_user, parent, false);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      holder.setData(getItem(position));

      return convertView;
    }
  }

  public static class ViewHolder {

    @InjectView(R.id.tv_name)
    public TextView mTvName;

    @InjectView(R.id.tv_info)
    public TextView mTvInfo;

    @InjectView(R.id.tv_circle_name)
    public TextView mTvCircleName;

    @InjectView(R.id.tv_timestamp)
    public TextView mTvTimestamp;

    @InjectView(R.id.aiv_portrait)
    public AsyncImageViewWithRoundCorner mAivPortrait;

    public void setData(PraisedUser user) {
      mTvName.setText(user.userName);
      mTvInfo.setText(String.format("最近已连续点赞%d次，共点赞%d次", user.consecutiveTimes, user.totalTimes));
      mTvTimestamp.setText(TimeUtil.getElapsed(user.timestamp));
      mTvCircleName.setText(user.workinFactory);
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

  }
}