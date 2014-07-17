package com.utree.eightysix.share;

import android.app.Activity;
import android.provider.ContactsContract;
import com.utree.eightysix.app.account.ContactsActivity;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
class ShareViaSMS implements IShare {
  @Override
  public void shareApp(Activity activity, int circleId) {
    ContactsActivity.start(activity, "to be implement");
  }

  @Override
  public void sharePost(Activity activity, Post post) {
    ContactsActivity.start(activity, "to be implement");
  }

  @Override
  public void shareComment(Activity activity, Post post, String comment) {
    ContactsActivity.start(activity, "to be implement");
  }
}
