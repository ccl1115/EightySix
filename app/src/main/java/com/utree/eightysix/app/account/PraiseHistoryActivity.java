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
import butterknife.OnClick;
import butterknife.OnItemClick;
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
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.List;

/**
 */
@Layout(R.layout.activity_praise_history)
public class PraiseHistoryActivity extends BaseActivity {

  private static final int PAGE_SIZE = 20;
  private Integer mViewId;
  private PraiseHistoryAdapter mAdapter;

  public static void start(Context context, int viewId, String gender) {
    Intent intent = new Intent(context, PraiseHistoryActivity.class);

    intent.putExtra("viewId", viewId);
    intent.putExtra("gender", gender);

    if (!(context instanceof Activity)) {
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.alv_praised_users)
  public AdvancedListView mAlvPraisedUsers;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  public int mPage = 1;
  public boolean mHasMore;

  @OnItemClick(R.id.alv_praised_users)
  public void onAlvPraisedUsersItemClicked(int position) {
    PraisedUser item = mAdapter.getItem(position);

    ProfileFragment.start(this, item.viewId, item.userName);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    showProgressBar();

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mViewId = getIntent().getIntExtra("viewId", -1);
    if (mViewId == -1) {
      mViewId = null;
    }
    final String gender = getIntent().getStringExtra("gender");

    if ("男".equals(gender)) {
      setTopTitle("谁赞过他");
    } else if ("女".equals(gender)) {
      setTopTitle("谁赞过她");
    } else {
      setTopTitle("谁赞过我");
    }

    mAlvPraisedUsers.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mHasMore;
      }

      @Override
      public boolean onLoadMoreStart() {
        mPage += 1;
        requestPraisedUsers();
        return true;
      }
    });

    requestPraisedUsers();
  }

  private void requestPraisedUsers() {
    U.request("praised_users", new OnResponse2<PraisedUsersResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(PraisedUsersResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object == null || response.object.size() == 0) {
            mHasMore = false;
            mRstvEmpty.setVisibility(View.VISIBLE);
            mAlvPraisedUsers.setAdapter(null);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mAdapter = new PraiseHistoryAdapter(response.object);
            mAlvPraisedUsers.setAdapter(mAdapter);
          }
        }
        hideProgressBar();
      }
    }, PraisedUsersResponse.class, mPage, PAGE_SIZE, mViewId);
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

  public class PraiseHistoryAdapter extends BaseAdapter {

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

  public class ViewHolder {

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

    @InjectView(R.id.tv_praise)
    public TextView mTvPraise;

    private PraisedUser mUser;

    @OnClick(R.id.tv_praise)
    public void onTvPraiseClicked() {
      U.request("user_praise_add", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {

        }

        @Override
        public void onResponse(Response response) {
          mUser.praised = 1;
          U.showToast("已回赞成功，对方经验+1");
          setData(mUser);
        }
      }, Response.class, mUser.viewId);
    }

    public void setData(PraisedUser user) {
      mUser = user;
      mTvName.setText(mUser.userName);
      mTvInfo.setText(String.format("最近已连续点赞%d次，共点赞%d次", user.consecutiveTimes, user.totalTimes));
      mTvTimestamp.setText(TimeUtil.getElapsed(mUser.timestamp));
      mTvCircleName.setText(mUser.workinFactory);
      mAivPortrait.setUrl(mUser.avatar);

      if (mViewId == null) {
        mTvPraise.setVisibility(View.VISIBLE);

        if (mUser.praised == 1) {
          mTvPraise.setText("已回赞");
          mTvPraise.setEnabled(false);
        } else if (mUser.praised == 0) {
          mTvPraise.setText("回赞");
          mTvPraise.setEnabled(true);
        }
      } else {
        mTvPraise.setVisibility(View.INVISIBLE);
      }
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

  }
}