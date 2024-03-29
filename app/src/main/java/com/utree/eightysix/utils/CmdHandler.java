/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import android.content.Context;
import android.content.Intent;
import com.utree.eightysix.Account;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.dp.DailyPicksActivity;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeTabActivity;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.msg.PraiseActivity;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.app.settings.HelpActivity;
import com.utree.eightysix.app.snapshot.SnapshotActivity;
import com.utree.eightysix.app.topic.TopicActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.data.Topic;

/**
 * feed:id
 * <p/>
 * post:id
 * <p/>
 * msg
 * <p/>
 * praise
 */
public class CmdHandler {
  public void handle(Context context, String cmd) {
    try {
      String[] args = cmd.split(":", 2);

      if (!HomeTabActivity.sIsRunning) {
        HomeTabActivity.start(context);
      }

      if ("feed".equals(args[0])) {
        FeedActivity.start(context, Integer.parseInt(args[1]));
      } else if ("post".equals(args[0])) {
        PostActivity.start(context, args[1]);
      } else if ("msg".equals(args[0])) {
        MsgActivity.start(context, true);
      } else if ("praise".equals(args[0])) {
        PraiseActivity.start(context, true);
      } else if ("topic-list".equals(args[0])) {
        TopicListActivity.start(context);
      } else if ("topic".equals(args[0])) {
        Topic topic = new Topic();
        topic.id = Integer.parseInt(args[1]);
        TopicActivity.start(context, topic);
      } else if ("tag".equals(args[0])) {
      } else if ("bs".equals(args[0])) {
        BaseWebActivity.start(context,
            String.format("http://c.lanmeiquan.com/activity/blueStar.do?userid=%s&token=%s",
                Account.inst().getUserId(),
                Account.inst().getToken()));
      } else if ("webview".equals(args[0])) {
        BaseWebActivity.start(context, args[1]);
      } else if ("snapshot-list".equals(args[0])) {
        BaseCirclesActivity.startSnapshot(context);
      } else if ("snapshot".equals(args[0])) {
        SnapshotActivity.start(context, Integer.parseInt(args[1]));
      } else if ("help".equals(args[0])) {
        context.startActivity(new Intent(context, HelpActivity.class));
      } else if ("dp".equals(args[0])) {
        DailyPicksActivity.start(context, 0);
      }
    } catch (Exception ignored) {

    }
  }

  private static CmdHandler sCmdHandler;

  public static CmdHandler inst() {
    if (sCmdHandler == null) {
      sCmdHandler = new CmdHandler();
    }

    return sCmdHandler;
  }
}
