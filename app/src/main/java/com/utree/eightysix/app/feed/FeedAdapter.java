package com.utree.eightysix.app.feed;

import com.squareup.otto.Subscribe;
import com.utree.eightysix.app.region.FeedRegionAdapter;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Feeds;
import com.utree.eightysix.data.OptionSet;
import com.utree.eightysix.data.Post;

import java.util.List;

/**
 */
public class FeedAdapter extends FeedRegionAdapter {

  public FeedAdapter(Feeds feeds) {
    mFeeds = feeds;

    boolean hasPosts = false;
    for (BaseItem item : mFeeds.posts.lists) {
      if (item.type == BaseItem.TYPE_POST) {
        hasPosts = true;
        break;
      }
    }

    if (mFeeds.selectFactory != 1) {
      // 设置在职企业
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_SELECT));
    } else if (mFeeds.upContact != 1) {
      // 上传通讯录
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_UPLOAD));
    } else if (mFeeds.current != 1) {
      // 不在职
      if (mFeeds.circle != null && mFeeds.circle.friendCount != 0 && mFeeds.lock == 1) {
        // 有朋友但没达到解锁条件
        mFeeds.posts.lists.add(0, new BaseItem(TYPE_UNLOCK));
      }
    } else if (!hasPosts) {
      // 邀请厂里的人加入
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_INVITE_FACTORY));
    } else if (mFeeds.circle != null && mFeeds.circle.friendCount == 0) {
      // 邀请朋友加入
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_INVITE_FRIEND));
    } else if (mFeeds.lock == 1) {
      // 锁定
      mFeeds.posts.lists.add(0, new BaseItem(TYPE_UNLOCK));
    }
  }

  @Subscribe
  public void onOptionSetEvent(OptionSet optionSet) {
    List<BaseItem> lists = mFeeds.posts.lists;
    for (int i = 0; i < lists.size(); i++) {
      BaseItem item = lists.get(i);
      if (item.type == BaseItem.TYPE_OPTION_SET) {
        lists.set(i, optionSet);
        break;
      }
    }
  }

  @Subscribe
  public void onPostEvent(Post post) {
    for (BaseItem item : mFeeds.posts.lists) {
      if (item == null || !(item instanceof Post)) continue;
      Post p = ((Post) item);
      if (p.equals(post)) {
        p.praise = post.praise;
        p.praised = post.praised;
        p.comments = post.comments;
        notifyDataSetChanged();
        break;
      }
    }
  }

  @Subscribe
  @Override
  public void onDismissTipOverlay(DismissTipOverlayEvent event) {
    switch (event.getType()) {
      case DismissTipOverlayEvent.TYPE_PRAISE:
        mTipPraisePosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_SOURCE:
        mTipSourcePosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_REPOST:
        mTipRepostPosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_TAGS:
        mTipTagsPosition = TNS;
        break;
      case DismissTipOverlayEvent.TYPE_TEMP_NAME:
        mTipTempNamePosition = TNS;
        break;
    }
  }
}
