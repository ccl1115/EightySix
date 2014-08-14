package com.utree.eightysix.app.share;

import android.app.Activity;
import com.utree.eightysix.Account;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
abstract class IShare {
  public abstract void shareApp(Activity activity, Circle circle);

  public abstract void sharePost(Activity activity, Circle circle, Post post);

  public abstract void shareComment(Activity activity, Circle circle, Post post, String comment);

  protected String shareLinkForApp(int circleId) {
    return String.format("%s/shareapp.do?userId=%s&factoryId=%d", U.getConfig("api.host"), Account.inst().getUserId(), circleId);
  }

  protected String shareLinkForPost(String postId) {
    return String.format("%s/sharecontent.do?userId=%s&postVirtualId=%s",
        U.getConfig("api.host"), Account.inst().getUserId(), postId);
  }

  protected String shareLinkForComment(String postId) {
    return shareLinkForPost(postId);
  }

  protected String shareTitleForApp() {
    return "和我一起玩【蓝莓】吧！";
  }

  protected String shareTitleForPost() {
    return "分享1个仁宝电脑的秘密";
  }

  protected String shareTitleForComment() {
    return "分享自［蓝莓圈］的精彩评论";
  }

  protected String shareContentForApp() {
    return "【蓝莓】-工厂里的秘密社区，仁宝电脑的朋友最近都在上面，旁边几个厂都火爆了，进来看看吧";
  }

  protected String shareContentForPost() {
    return "转自【蓝莓】-工厂里的秘密社区";
  }
}
