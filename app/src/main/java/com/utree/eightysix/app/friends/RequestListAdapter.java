/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.os.Bundle;
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
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.account.ProfileFillDialog;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.data.FriendRequest;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AsyncImageView;
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

  public void update(FriendRequest request) {
    for (int i = 0; i < mRequest.size(); i++) {
      FriendRequest r = mRequest.get(i);
      if (r.equals(request)) {
        mRequest.set(i, request);
      }
    }
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

    @InjectView(R.id.aiv_portrait)
    public AsyncImageView mAivPortrait;

    @OnClick({R.id.aiv_portrait, R.id.tv_name})
    public void onAivPortraitClicked(View v) {
      Bundle args = new Bundle();
      args.putInt("viewId", mRequest.viewId);
      args.putBoolean("isVisitor", true);
      args.putString("userName", mRequest.userName);
      FragmentHolder.start(v.getContext(), ProfileFragment.class, args);
    }

    private FriendRequest mRequest;

    @OnClick(R.id.rb_accept)
    public void onRbAcceptClicked(final View view) {

      mRequest.type = "passed";
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
          mRequest.type = "added";
          U.getBus().post(mRequest);
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
            mRequest.type = "added";
          }
          U.getBus().post(mRequest);
        }
      }, Response.class, mRequest.viewId);
    }

    @OnClick(R.id.tv_ignore)
    public void onTvIgnoreClicked() {

      mRequest.type = "ignore";
      mTvResult.setVisibility(View.VISIBLE);
      mRbAccept.setVisibility(View.GONE);
      mTvIgnore.setVisibility(View.GONE);
      mTvResult.setText("已忽略");

      U.request("user_friend_ignore", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {
          mRbAccept.setVisibility(View.VISIBLE);
          mTvIgnore.setVisibility(View.VISIBLE);
          mTvResult.setVisibility(View.GONE);
          mRequest.type = "added";
          U.getBus().post(mRequest);
        }

        @Override
        public void onResponse(Response response) {
          if (!RESTRequester.responseOk(response)) {
            mRbAccept.setVisibility(View.VISIBLE);
            mTvIgnore.setVisibility(View.VISIBLE);
            mTvResult.setVisibility(View.GONE);
            mRequest.type = "added";
          }
          U.getBus().post(mRequest);
        }
      }, Response.class, mRequest.viewId);
    }

    public void setData(FriendRequest request) {
      mRequest = request;

      mAivPortrait.setUrl(mRequest.avatar);
      mTvName.setText(mRequest.userName);
      mTvInfo.setText(mRequest.content);
      mTvTimestamp.setText(TimeUtil.getElapsed(mRequest.timestamp));
      if ("added".equals(mRequest.type)) {
        mRbAccept.setVisibility(View.VISIBLE);
        mTvIgnore.setVisibility(View.VISIBLE);
        mTvResult.setVisibility(View.GONE);
      } else if ("ignore".equals(mRequest.type)) {
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

    public RequestViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
