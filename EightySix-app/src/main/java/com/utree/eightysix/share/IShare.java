package com.utree.eightysix.app.share.share;

import android.app.Activity;
import com.utree.eightysix.Account;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public abstract class IShare {
  public abstract void shareApp(Activity activity, int circleId);

  public abstract void sharePost(Activity activity, Post post);

  public abstract void shareComment(Activity activity, Post post, String comment);

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
    return "来自［蓝莓圈］";
  }

  protected String shareTitleForPost() {
    return "分享自［蓝莓圈］的精华帖";
  }

  protected String shareTitleForComment() {
    return "分享自［蓝莓圈］的精彩评论";
  }

  protected String shareContentForApp() {
    return "“快来和我一起玩［蓝莓圈］吧，里面有你喜欢的朋友圈子，身边的好友都在圈子里匿名爆料，八卦，说秘密，不要错过精彩内容哦”";
  }
}
