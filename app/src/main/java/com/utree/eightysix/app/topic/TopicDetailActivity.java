/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
@Layout(R.layout.activity_topic_detail)
public class TopicDetailActivity extends BaseActivity {


  @InjectView(R.id.tv_title)
  public TextView mTvTitle;

  @InjectView(R.id.rb_text)
  public RoundedButton mRbText;

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

    Topic topic = getIntent().getParcelableExtra("topic");

    mTvTitle.setText(topic.title);
    mRbText.setText(topic.content);
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