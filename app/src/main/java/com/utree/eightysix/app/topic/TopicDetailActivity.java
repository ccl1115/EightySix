/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.data.Topic;

/**
 */
@Layout(R.layout.activity_topic_detail)
@TopTitle(R.string.topic_desc)
public class TopicDetailActivity extends BaseActivity {


  @InjectView(R.id.tv_title)
  public TextView mTvTitle;

  @InjectView(R.id.tv_text)
  public TextView mTvText;

  @InjectView(R.id.v_line)
  public View mVLine;

  public static void start(Context context, Topic topic) {
    Intent intent = new Intent(context, TopicDetailActivity.class);

    intent.putExtra("topic", topic);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    Topic topic = getIntent().getParcelableExtra("topic");

    if (TextUtils.isEmpty(topic.title)) {
      mTvTitle.setVisibility(View.GONE);
      mVLine.setVisibility(View.GONE);
    } else {
      mTvTitle.setVisibility(View.VISIBLE);
      mVLine.setVisibility(View.VISIBLE);
    }
    mTvTitle.setText(topic.title);
    mTvText.setText(topic.topicDesc);
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
}