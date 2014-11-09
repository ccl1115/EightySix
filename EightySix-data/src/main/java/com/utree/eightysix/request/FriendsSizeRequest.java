package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_FACTORY_FRIEND_SIZE)
@Token
public class FriendsSizeRequest {

  @Param("factoryId")
  public int factoryId;

  public FriendsSizeRequest(int factoryId) {
    this.factoryId = factoryId;
  }
}
