package com.utree.eightysix.share;

import android.app.Activity;
import android.provider.ContactsContract;
import com.utree.eightysix.app.account.ContactsActivity;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
class ShareViaSMS extends IShare {
  @Override
  public void shareApp(Activity activity, int circleId) {
    ContactsActivity.start(activity, String.format("%s，%s", shareContentForApp(), shareLinkForApp(circleId)));
  }

  @Override
  public void sharePost(Activity activity, Post post) {
    ContactsActivity.start(activity, String.format("“%s”，%s", post.content, shareLinkForPost(post.id)));
  }

  @Override
  public void shareComment(Activity activity, Post post, String comment) {
    ContactsActivity.start(activity, String.format("“%s”，%s", comment, shareLinkForComment(post.id)));
  }
}
