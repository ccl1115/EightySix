/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

/**
 */

@Layout(R.layout.activity_home_tab)
public class HomeTabActivity extends BaseActivity {

  public static void start(Context context) {
    Intent i = new Intent(context, HomeTabActivity.class);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(i);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActionLeftClicked() {

  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}