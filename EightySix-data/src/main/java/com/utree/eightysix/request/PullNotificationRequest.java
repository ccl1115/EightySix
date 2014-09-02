package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_NOTIFICATION)
@Token
public class PullNotificationRequest {

  /**
   * 1 POST
   * 2 UNLOCK_FACTORY
   * 3 FRIEND_L1_JOIN
   * 4 NEW_COMMENT
   * 5 NEW_PRAISE
   */
  @Param("type")
  public int type;

  @Param("pushSeq")
  public String pushSeq;

  public PullNotificationRequest(int type, String pushSeq) {
    this.type = type;
    this.pushSeq = pushSeq;
  }
}
