/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.snapshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.widget.TitleTab;
import com.utree.eightysix.widget.TopBar;

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

    mTopBar.setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return "动态";
      }

      @Override
      public Drawable getIcon(int position) {
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return new RoundRectDrawable(dp2px(2), getResources().getColorStateList(R.color.apptheme_primary_btn_light));
      }

      @Override
      public void onClick(View view, int position) {
        FeedActivity.start(SnapshotActivity.this, mCircle);
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
      }
    });

    onNewIntent(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    setTopTitle(mCircle.shortName);
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
