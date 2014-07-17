package com.utree.eightysix.share;

import android.app.Activity;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public interface IShare {
  void shareApp(Activity activity, int circleId);

  void sharePost(Activity activity, Post post);

  void shareComment(Activity activity, Post post, String comment);
}
