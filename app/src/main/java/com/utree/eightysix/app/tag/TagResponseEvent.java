package com.utree.eightysix.app.tag;

import com.utree.eightysix.data.Tag;

/**
 */
public class TagResponseEvent {
  private Tag mTag;

  public TagResponseEvent(Tag tag) {
    mTag = tag;
  }

  public Tag getTag() {
    return mTag;
  }
}
