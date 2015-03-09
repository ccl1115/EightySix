/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.view.View;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.circle.BaseCirclesActivity;

/**
 */
@Layout(R.layout.activity_profile_fill)
@TopTitle(R.string.profile_fill)
public class ProfileFillActivity extends BaseActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbRight().setText(getString(R.string.skip));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        BaseCirclesActivity.startSelect(ProfileFillActivity.this, false);
        finish();
      }
    });
    getTopBar().getAbLeft().hide();
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onBackPressed() {
  }
}