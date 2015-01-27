/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.snapshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.widget.TitleTab;

/**
 */
@Layout(R.layout.activity_tag_tab)
public class SnapshotActivity extends BaseActivity {

  @InjectView(R.id.tt_tab)
  public TitleTab mTitleTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  private Circle mCircle;

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, SnapshotActivity.class);

    intent.putExtra("circle", circle);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mCircle = getIntent().getParcelableExtra("circle");

    if (mCircle == null) {
      finish();
      return;
    }
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
