/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.response.ProfileResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

/**
 */
@Layout(R.layout.activity_send_request)
@TopTitle(R.string.friend_request)
public class SendRequestActivity extends BaseActivity {

  public static void start(Context context, int viewId) {
    Intent intent = new Intent(context, SendRequestActivity.class);

    intent.putExtra("viewId", viewId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.et_content)
  public EditText mEtContent;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final int viewId = getIntent().getIntExtra("viewId", 0);

    U.request("profile", new OnResponse2<ProfileResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(ProfileResponse response) {
        mEtContent.setText("你好，我是" + response.object.userName + "，添加我为蓝莓朋友吧");
        mEtContent.setSelection(mEtContent.getText().length());
      }
    }, ProfileResponse.class, (Integer) null);


    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    getTopBar().getAbRight().setText("发送");
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        U.request("user_friend_request", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              showToast("发送成功", false);
              U.getBus().post(new SentRequestEvent(viewId));
              finish();
            }
          }
        }, Response.class, viewId, mEtContent.getText().toString());
      }
    });
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

  public static class SentRequestEvent {

    private int mViewId;

    public SentRequestEvent(int viewId) {

      mViewId = viewId;
    }

    public int getViewId() {
      return mViewId;
    }
  }
}