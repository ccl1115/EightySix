package com.utree.eightysix.app.share;

import android.app.Activity;
import com.utree.eightysix.app.account.ContactsActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
class ShareViaSMS extends IShare {
  @Override
  public void shareApp(Activity activity, Circle circle) {
    ContactsActivity.start(activity,
        String.format(shareContentForApp(), circle.shortName, circle.shortName) + shareLinkForApp(circle.id));
  }

  @Override
  public void sharePost(Activity activity, Circle circle, Post post) {
    ContactsActivity.start(activity,
        String.format(shareContentForPost(), circle.shortName, circle.shortName) + shareLinkForPost(post.id));
  }

  @Override
  public void shareComment(Activity activity, Circle circle, Post post, String comment) {
    ContactsActivity.start(activity,
        String.format("“%s”，%s", comment, shareLinkForComment(post.id)));
  }

  protected String shareContentForApp() {
    return "%s的某同事匿名邀请你加入【蓝莓-%s圈】。点击查看-";
  }

  protected String shareContentForPost() {
    return "%s的同事匿名给你分享了1条厂里的秘密，点击查看-";
  }
}
