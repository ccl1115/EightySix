package com.utree.eightysix.request;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.C;
import com.utree.eightysix.data.ActiveJoin;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_ACTIVE_JOIN)
@Token
public class ActiveJoinRequest {

  @SerializedName("object")
  public ActiveJoin object;
}
