/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout(R.layout.activity_base_web)
public class BainianWebActivity extends BaseWebActivity {

  public static void start(Context context, String recipient, String msg) {

    String url = String.format("http://203.195.217.85/wapui/index.php/share/greetingCards?to=%s&content=%s&send=%s",
        recipient, msg, "inner");
    Intent intent = new Intent(context, BainianWebActivity.class);
    intent.putExtra("title", "蓝莓专属拜年卡");
    intent.putExtra("url", url);
    intent.putExtra("recipient", recipient);
    intent.putExtra("msg", msg);

    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return null;
      }

      @Override
      public Drawable getIcon(int position) {
        return getResources().getDrawable(R.drawable.ic_topbar_share);
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_transparent_bg);
      }

      @Override
      public void onClick(View view, int position) {
        U.getShareManager().shareBainianDialog(BainianWebActivity.this,
            getIntent().getStringExtra("recipient"),
            getIntent().getStringExtra("msg")).show();
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }
    });

    mGvLoading.setVisibility(View.INVISIBLE);
  }
}
