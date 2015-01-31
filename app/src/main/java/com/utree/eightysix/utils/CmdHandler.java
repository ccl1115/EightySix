/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import android.content.Context;
import com.utree.eightysix.Account;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.msg.PraiseActivity;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.app.tag.TagTabActivity;
import com.utree.eightysix.app.topic.TopicActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.data.Topic;

/**
 * feed:id
 *
 * post:id
 *
 * msg
 *
 * praise
 *
 */
public class CmdHandler {
  public void handle(Context context, String cmd) {
    String[] args = cmd.split(":");

    if ("feed".equals(args[0])) {
      HomeActivity.start(context);
      FeedActivity.start(context, Integer.parseInt(args[1]));
    } else if ("post".equals(args[0])) {
      HomeActivity.start(context);
      PostActivity.start(context, args[1]);
    } else if ("msg".equals(args[0])) {
      HomeActivity.start(context);
      MsgActivity.start(context, true);
    } else if ("praise".equals(args[0])) {
      HomeActivity.start(context);
      PraiseActivity.start(context, true);
    } else if ("topic-list".equals(args[0])) {
      HomeActivity.start(context);
      TopicListActivity.start(context);
    } else if ("topic".equals(args[0])) {
      HomeActivity.start(context);
      Topic topic = new Topic();
      topic.id = Integer.parseInt(args[1]);
      TopicActivity.start(context, topic);
    } else if ("tag".equals(args[0])) {
      HomeActivity.start(context);
      TagTabActivity.start(context, Integer.parseInt(args[1]));
    } else if ("bs".equals(args[0])) {
      HomeActivity.start(context);
      BaseWebActivity.start(U.getContext(),
          String.format("http://c.lanmeiquan.com/activity/blueStar.do?userid=%s&token=%s",
              Account.inst().getUserId(),
              Account.inst().getToken()));
    } else if ("webview".equals(args[0])) {
      HomeActivity.start(context);
      BaseWebActivity.start(U.getContext(), args[1]);
    }
  }
}
