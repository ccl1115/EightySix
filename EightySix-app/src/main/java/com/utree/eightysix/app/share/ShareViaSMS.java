package com.utree.eightysix.app.share;

import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.account.ContactsActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
class ShareViaSMS extends IShare {
  @Override
  public void shareApp(BaseActivity activity, Circle circle, String url) {
    ContactsActivity.start(activity,
        String.format(shareContentForApp(), circle.shortName, circle.shortName) + url);
  }

  @Override
  public void sharePost(BaseActivity activity, Post post, String url) {
    ContactsActivity.start(activity,
        String.format(shareContentForPost(), post.shortName, post.shortName) + url);
  }

  @Override
  public void shareComment(BaseActivity activity, Post post, String comment, String url) {
    ContactsActivity.start(activity, String.format("“%s”，%s", comment, url));
  }

  @Override
  public void shareTag(BaseActivity activity, Circle circle, int tagId, String url) {
    ContactsActivity.start(activity,
        String.format(shareContentForApp(), circle.shortName, circle.shortName) + url);
  }

  protected String shareContentForApp() {
    return "%s的某同事匿名邀请你加入【蓝莓-%s圈】。点击查看-";
  }

  protected String shareContentForPost() {
    return "%s的同事匿名给你分享了1条厂里的秘密，点击查看-";
  }
}
