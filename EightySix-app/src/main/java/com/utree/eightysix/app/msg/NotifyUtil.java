package com.utree.eightysix.app.msg;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.push.PushMessageReceiver;

/**
 * @author simon
 */
public class NotifyUtil {

  static final int ID_POST = 0x1000;
  static final int ID_UNLOCK_FACTORY = 0x2000;
  static final int ID_FRIEND_L1_JOIN = 0x3000;
  static final int ID_FOLLOW_COMMENT = 0x4000;
  static final int ID_PRAISE = 0x5000;
  static final int ID_APPROVE = 0x6000;
  static final int ID_OWN_COMMENT = 0x7000;
  private static Bitmap sLargeIcon;

  static {
    sLargeIcon = BitmapFactory.decodeResource(U.getContext().getResources(), R.drawable.ic_launcher);
  }

  private final Context mContext;

  NotifyUtil(Context context) {
    mContext = context;
  }

  private Intent[] wrapIntent(Intent... intents) {
    Intent[] wrap = new Intent[intents.length + 1];
    wrap[0] = HomeActivity.getIntent(mContext);

    System.arraycopy(intents, 0, wrap, 1, intents.length);
    return wrap;
  }

  Notification buildPost(int i, String postId, String shortName) {
    Log.d(C.TAG.NT, "build post: " + postId);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setTicker(mContext.getString(R.string.notification_friend_new_post))
        .setAutoCancel(true)
        .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(shortName)
        .setContentText(mContext.getString(R.string.notification_friend_new_post));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      builder.setContentIntent(PendingIntent.getActivities(mContext, 0,
          wrapIntent(PostActivity.getIntent(mContext, postId, "post_", false)),
          PendingIntent.FLAG_UPDATE_CURRENT));
    } else {
      builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
          PostActivity.getIntent(mContext, postId, "post_", false),
          PendingIntent.FLAG_UPDATE_CURRENT));
    }


    return builder.build();
  }

  Notification buildPosts(String shortName, boolean current, int circleId, int count) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setTicker(mContext.getString(R.string.notification_friends_new_posts, count))
        .setAutoCancel(true)
        .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(shortName)
        .setContentText(mContext.getString(R.string.notification_friends_new_posts, count));
    if (current) {
      builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
          HomeActivity.getIntent(mContext),
          PendingIntent.FLAG_UPDATE_CURRENT));
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivities(mContext, 0,
            wrapIntent(FeedActivity.getIntent(mContext, circleId, true)),
            PendingIntent.FLAG_UPDATE_CURRENT));
      } else {
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
            FeedActivity.getIntent(mContext, circleId, true),
            PendingIntent.FLAG_UPDATE_CURRENT));
      }
    }

    return builder.build();
  }

  Notification buildUnlockCircle(String circleId, String circleName, boolean current) {
    Log.d(C.TAG.NT, "build unlock circle: " + circleId);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setTicker(mContext.getString(R.string.notification_circle_unlocked))
        .setLargeIcon(sLargeIcon)
        .setAutoCancel(true)
        .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(mContext.getString(R.string.notification_circle_unlocked))
        .setContentText(mContext.getString(R.string.notification_circle_unlocked_tip, circleName));

    if (current) {
      builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
          HomeActivity.getIntent(mContext), PendingIntent.FLAG_UPDATE_CURRENT));
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivities(mContext, 0,
            wrapIntent(FeedActivity.getIntent(mContext, Integer.parseInt(circleId), true)),
            PendingIntent.FLAG_UPDATE_CURRENT));
      } else {
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
            FeedActivity.getIntent(mContext, Integer.parseInt(circleId), true),
            PendingIntent.FLAG_UPDATE_CURRENT));
      }
    }

    return builder.build();
  }

  Notification buildComment(int count, String id, int type, boolean current, int factoryId) {
    Log.d(C.TAG.NT, String.format("build comment: count = %d id = %s", count, id));
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setContentTitle(mContext.getString(R.string.notification_new))
        .setAutoCancel(true)
        .setTicker(mContext.getString(R.string.notification_new))
        .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setSmallIcon(R.drawable.ic_launcher)
        .setLargeIcon(sLargeIcon);
    if (count == 1) {
      builder.setContentText(mContext.getString(type == PushMessageReceiver.TYPE_FOLLOW_COMMENT ?
          R.string.notification_new_follow_comment : R.string.notification_new_own_comment));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        if (current) {
          builder.setContentIntent(PendingIntent.getActivities(mContext, 0,
              wrapIntent(PostActivity.getIntent(mContext, id, "comment_", true)),
              PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
          builder.setContentIntent(PendingIntent.getActivities(mContext, 0,
              wrapIntent(FeedActivity.getIntent(mContext, factoryId, true),
                  PostActivity.getIntent(mContext, id, "comment_", true)),
              PendingIntent.FLAG_UPDATE_CURRENT));
        }
      } else {
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
            PostActivity.getIntent(mContext, id, "comment_", true),
            PendingIntent.FLAG_UPDATE_CURRENT));
      }
    } else {
      builder.setContentText(mContext.getString(type == PushMessageReceiver.TYPE_FOLLOW_COMMENT ?
          R.string.notification_new_follow_comments : R.string.notification_new_own_comments, count));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivities(mContext, 0,
            wrapIntent(MsgActivity.getIntent(mContext, true)), PendingIntent.FLAG_UPDATE_CURRENT));
      } else {
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
            MsgActivity.getIntent(mContext, true), PendingIntent.FLAG_UPDATE_CURRENT));
      }
    }
    return builder.build();
  }

  Notification buildApprove(String circleId, String circleName) {
    Log.d(C.TAG.NT, "build approve: " + circleId);
    return new NotificationCompat.Builder(mContext)
        .setDefaults(Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon)
        .setAutoCancel(true)
        .setTicker(mContext.getString(R.string.notification_circle_create_approve))
        .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(mContext.getString(R.string.notification_circle_create_approve))
        .setContentText(mContext.getString(R.string.notification_circle_create_approve_tip, circleName))
        .setContentIntent(PendingIntent.getActivity(mContext, 0,
            FeedActivity.getIntent(mContext, Integer.parseInt(circleId), true), PendingIntent.FLAG_UPDATE_CURRENT))
        .build();
  }

  Notification buildFriendJoin(String circleId, String circleName, boolean current) {
    Log.d(C.TAG.NT, "build friend join: " + circleId);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder
        .setLargeIcon(sLargeIcon)
        .setAutoCancel(true)
        .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setSmallIcon(R.drawable.ic_launcher)
        .setTicker(mContext.getString(R.string.notification_new_friend))
        .setContentTitle(mContext.getString(R.string.notification_new_friend))
        .setContentText(mContext.getString(R.string.notification_new_friend_tip, circleName));

    if (current) {
      builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
          HomeActivity.getIntent(mContext), PendingIntent.FLAG_UPDATE_CURRENT));
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivities(mContext, 0,
            wrapIntent(FeedActivity.getIntent(mContext, Integer.parseInt(circleId), true)),
            PendingIntent.FLAG_UPDATE_CURRENT));
      } else {
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
            FeedActivity.getIntent(mContext, Integer.parseInt(circleId), true),
            PendingIntent.FLAG_UPDATE_CURRENT));
      }
    }

    return builder.build();
  }
}
