/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.event.NameUpdatedEvent;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

/**
 */
@Layout(R.layout.activity_name_edit)
public class NameEditActivity extends BaseActivity {

  public static void start(Context context, String name) {
    Intent intent = new Intent(context, NameEditActivity.class);
    intent.putExtra("name", name);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.et_name)
  public EditText mEtName;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String name = getIntent().getStringExtra("name");

    if (!TextUtils.isEmpty(name)) {
      mEtName.setText(name);
      mEtName.setSelection(name.length());
    }

    getTopBar().getAbRight().setText(getString(R.string.submit));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String name = mEtName.getText().toString();
        Utils.updateProfile(null, name, null, null, null, null, null, null,
            new OnResponse2<Response>() {
              @Override
              public void onResponseError(Throwable e) {

              }

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  U.getBus().post(new NameUpdatedEvent(name));
                  finish();
                }
              }
            });
      }
    });

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}