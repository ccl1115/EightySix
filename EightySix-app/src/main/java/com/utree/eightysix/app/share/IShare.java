package com.utree.eightysix.app.share;

import android.app.Activity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
abstract class IShare {

  String mShortenAppLink;
  String mShortenPostLink;

  public abstract void shareApp(Activity activity, Circle circle, String url);

  public abstract void sharePost(Activity activity, Post post, String url);

  public abstract void shareComment(Activity activity, Post post, String comment, String url);


  protected String shareTitleForApp() {
    return "和我一起玩【蓝莓】吧！";
  }

  protected String shareTitleForPost() {
    return "分享1个%s的秘密";
  }

  protected String shareTitleForComment() {
    return "分享自【蓝莓】的精彩评论";
  }

  protected String shareContentForApp() {
    return "%s的朋友最近都在上面，旁边几个厂都火爆了，进来看看吧";
  }

  protected String shareContentForPost() {
    return "转自【蓝莓】-工厂里的秘密社区";
  }
}
