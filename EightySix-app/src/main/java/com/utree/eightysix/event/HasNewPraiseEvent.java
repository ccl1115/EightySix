package com.utree.eightysix.event;

/**
 * Fire when PullNotificationService fetch a new praise notification
 *
 * @author simon
 */
public class HasNewPraiseEvent {
  private boolean mHas;

  public boolean has() {
    return mHas;
  }

  public HasNewPraiseEvent(boolean has) {

    mHas = has;
  }
}
