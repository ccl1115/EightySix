/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.ProfileFillDialog;
import com.utree.eightysix.data.FriendRequest;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;

/**
 */
@Layout(R.layout.activity_request_detail)
@TopTitle(R.string.friend_request)
public class RequestDetailActivity extends BaseActivity {

  private FriendRequest mRequest;

  public static void start(Context context, FriendRequest request) {
    Intent intent = new Intent(context, RequestDetailActivity.class);
    intent.putExtra("request", request);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.aiv_portrait)
  public AsyncImageViewWithRoundCorner mAivPortrait;

  @InjectView(R.id.tv_name)
  public TextView mTvName;

  @InjectView(R.id.tv_info)
  public TextView mTvInfo;

  @InjectView(R.id.tv_timestamp)
  public TextView mTvTimestamp;

  @InjectView(R.id.tv_result)
  public TextView mTvResult;

  @InjectView(R.id.rb_accept)
  public TextView mRbAccept;

  @InjectView(R.id.tv_ignore)
  public TextView mTvIgnore;

  @OnClick(R.id.rb_accept)
  public void onRbAcceptClicked(final View view) {

    mTvResult.setVisibility(View.VISIBLE);
    mRbAccept.setVisibility(View.GONE);
    mTvIgnore.setVisibility(View.GONE);
    mTvResult.setText("已同意");

    U.request("user_friend_accept", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
        mRbAccept.setVisibility(View.VISIBLE);
        mTvIgnore.setVisibility(View.VISIBLE);
        mTvResult.setVisibility(View.GONE);
      }

      @Override
      public void onResponse(Response response) {
        if (!RESTRequester.responseOk(response)) {
          if (response.code == 0x10AA) {
            new ProfileFillDialog(view.getContext()).show();
          }
          mRbAccept.setVisibility(View.VISIBLE);
          mTvIgnore.setVisibility(View.VISIBLE);
          mTvResult.setVisibility(View.GONE);
        }
      }
    }, Response.class, mRequest.viewId);
  }

  @OnClick(R.id.tv_ignore)
  public void onTvIgnoreClicked() {

    mRbAccept.setVisibility(View.GONE);
    mTvIgnore.setVisibility(View.GONE);
    mTvResult.setText("已忽略");

    U.request("user_friend_ignore", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
        mRbAccept.setVisibility(View.VISIBLE);
        mTvIgnore.setVisibility(View.VISIBLE);
        mTvResult.setVisibility(View.GONE);
      }

      @Override
      public void onResponse(Response response) {
        if (!RESTRequester.responseOk(response)) {
          mRbAccept.setVisibility(View.VISIBLE);
          mTvIgnore.setVisibility(View.VISIBLE);
          mTvResult.setVisibility(View.GONE);
        }
      }
    }, Response.class, mRequest.viewId);
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mRequest = getIntent().getParcelableExtra("request");

    mAivPortrait.setUrl(mRequest.avatar);
    mTvName.setText(mRequest.userName);
    mTvInfo.setText("附加信息：" + mRequest.content);
    mTvTimestamp.setText(TimeUtil.getElapsed(mRequest.timestamp));
    if ("added".equals(mRequest.type)) {
      mRbAccept.setVisibility(View.VISIBLE);
      mTvIgnore.setVisibility(View.VISIBLE);
      mTvResult.setVisibility(View.GONE);
    } else if ("ignored".equals(mRequest.type)) {
      mTvResult.setVisibility(View.VISIBLE);
      mRbAccept.setVisibility(View.GONE);
      mTvIgnore.setVisibility(View.GONE);
      mTvResult.setText("已忽略");
    } else if ("passed".equals(mRequest.type)) {
      mTvResult.setVisibility(View.VISIBLE);
      mRbAccept.setVisibility(View.GONE);
      mTvIgnore.setVisibility(View.GONE);
      mTvResult.setText("已同意");
    }
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
}