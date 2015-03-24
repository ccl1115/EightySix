/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.event.SignatureUpdatedEvent;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ThemedDialog;

/**
 */
@Layout(R.layout.activity_signature_edit)
public class SignatureEditActivity extends BaseActivity {

  private boolean mTextChanged;
  private String mText;

  public static void start(Context context, String text) {
    Intent intent = new Intent(context, SignatureEditActivity.class);
    intent.putExtra("text", text);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.et_signature)
  public EditText mEtSignature;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mText = getIntent().getStringExtra("text");

    mEtSignature.setText(mText);
    mEtSignature.setSelection(mText.length());

    mEtSignature.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTextChanged = !s.equals(mText);
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));
    getTopBar().getAbRight().setText(getString(R.string.submit));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String signature = mEtSignature.getText().toString();
        Utils.updateProfile(null, null, null, null, null, null, signature, null,
            new OnResponse2<Response>() {
              @Override
              public void onResponseError(Throwable e) {

              }

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  U.getBus().post(new SignatureUpdatedEvent(signature));
                  finish();
                }
              }
            });
      }
    });
  }

  @Override
  public void onActionLeftClicked() {
    if (mTextChanged) {
      showQuitConfirmDialog();
    } else {
      finish();
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onBackPressed() {
    if (mTextChanged) {
      showQuitConfirmDialog();
    } else {
      super.onBackPressed();
    }
  }

  private void showQuitConfirmDialog() {
    final ThemedDialog dialog = new ThemedDialog(this);

    dialog.setTitle("你的签名已改动，确认不提交？");

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        finish();
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

}