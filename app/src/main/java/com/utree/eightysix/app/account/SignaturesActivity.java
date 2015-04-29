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
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.response.UserSignaturesResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.ThemedDialog;

import java.util.List;

/**
 */
@Layout(R.layout.activity_signatures)
public class SignaturesActivity extends BaseActivity {

  public static void start(Context context, boolean isVisitor, int viewId, String gender) {
    Intent intent = new Intent(context, SignaturesActivity.class);

    intent.putExtra("isVisitor", isVisitor);
    intent.putExtra("viewId", viewId);
    intent.putExtra("gender", gender);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);

  }

  @InjectView(R.id.alv_signatures)
  public AdvancedListView mAlvSignatures;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private SignaturesAdapter mAdapter;

  private boolean mIsVisitor;
  private int mViewId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mIsVisitor = getIntent().getBooleanExtra("isVisitor", false);
    mViewId = getIntent().getIntExtra("viewId", -1);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    if (mIsVisitor) {
      boolean equals = getIntent().getStringExtra("gender").equals("男");
      String gender = equals ? "他" : "她";
      setTopTitle(gender + "的签名");
      mRstvEmpty.setText(gender + "很懒，还没有签名呃");
    } else {
      setTopTitle("我的签名");
      getTopBar().getAbRight().setText("新增");
      getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          SignatureEditActivity.start(SignaturesActivity.this, "");
        }
      });
      mRstvEmpty.setText("还没有签名");
      mRstvEmpty.setSubText("介绍一下自己，或说说你的小心情吧");
    }

  }

  @Override
  protected void onResume() {
    super.onResume();

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
            mAdapter = new SignaturesAdapter(response.object);
            mAlvSignatures.setAdapter(mAdapter);
          }
        }
      }
    }, UserSignaturesResponse.class, mIsVisitor ? mViewId : null);
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

  private void showDeleteConfirmDialog(final UserSignaturesResponse.Signature signature) {
    final ThemedDialog dialog = new ThemedDialog(this);

    dialog.setTitle("确认删除此条签名？");

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        showProgressBar(true);
        U.request("user_signature_del", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {
            hideProgressBar();
          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              mAdapter.remove(signature);
            }

            hideProgressBar();
          }
        }, Response.class, signature.id);
      }
    });

    dialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  public class SignaturesAdapter extends BaseAdapter {

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

    public void remove(UserSignaturesResponse.Signature signature) {
      mSignatures.remove(signature);
      notifyDataSetChanged();
    }
  }

  public class ViewHolder {

    @InjectView(R.id.tv_content)
    public TextView mTvContent;

    @InjectView(R.id.tv_timestamp)
    public TextView mTvTimestamp;

    @InjectView(R.id.tv_delete)
    public TextView mTvDelete;

    private UserSignaturesResponse.Signature mSignature;

    @OnClick(R.id.tv_delete)
    public void onTvDeleteClicked() {
      showDeleteConfirmDialog(mSignature);
    }

    public void setData(UserSignaturesResponse.Signature signature) {
      mSignature = signature;
      mTvContent.setText(mSignature.signature);
      mTvTimestamp.setText(TimeUtil.getElapsed(mSignature.timestamp));
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);

      if (mIsVisitor) {
        mTvDelete.setVisibility(View.GONE);
      } else {
        mTvDelete.setVisibility(View.VISIBLE);
      }
    }
  }
}