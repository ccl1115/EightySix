/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.response.UserSignaturesResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.List;

/**
 */
@Layout(R.layout.activity_signatures)
@TopTitle(R.string.my_signatures)
public class SignaturesActivity extends BaseActivity {

  @InjectView(R.id.alv_signatures)
  public AdvancedListView mAlvSignatures;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().getAbRight().setText("新增");
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SignatureEditActivity.start(SignaturesActivity.this, "");
      }
    });

    mRstvEmpty.setDrawable(R.drawable.scene_4);
    mRstvEmpty.setText("还没有签名");

    showProgressBar(true);
    U.request("user_signatures", new OnResponse2<UserSignaturesResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(UserSignaturesResponse response) {
        hideProgressBar();

        if (RESTRequester.responseOk(response)) {
          if (response.object.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mAlvSignatures.setAdapter(new SignaturesAdapter(response.object));
          }
        }
      }
    }, UserSignaturesResponse.class);
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

  public static class SignaturesAdapter extends BaseAdapter {

    private List<UserSignaturesResponse.Signature> mSignatures;

    public SignaturesAdapter(List<UserSignaturesResponse.Signature> signatures) {
      mSignatures = signatures;
    }

    @Override
    public int getCount() {
      return mSignatures.size();
    }

    @Override
    public UserSignaturesResponse.Signature getItem(int position) {
      return mSignatures.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if (convertView == null) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signature, parent, false);
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

    @InjectView(R.id.tv_content)
    public TextView mTvContent;

    @InjectView(R.id.tv_timestamp)
    public TextView mTvTimestamp;
    private UserSignaturesResponse.Signature mSignature;

    @OnClick(R.id.tv_delete)
    public void onTvDeleteClicked() {

    }

    public void setData(UserSignaturesResponse.Signature signature) {
      mSignature = signature;
      mTvContent.setText(mSignature.signature);
      mTvTimestamp.setText(TimeUtil.getDate(mSignature.timestamp));
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}