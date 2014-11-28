/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_ADD_FRIEND)
@Token
public class AddFriendRequest {

  @Param("otherId")
  public String otherId;

  /**
   * 1 qr code scan
   * 2 by user id
   */
  @Param("pf")
  public int source;

  public AddFriendRequest(String otherId, int source) {
    this.otherId = otherId;
    this.source = source;
  }
}
