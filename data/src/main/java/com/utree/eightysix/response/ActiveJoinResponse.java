package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.ActiveJoin;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class ActiveJoinResponse extends Response {

  @SerializedName("object")
  public ActiveJoin object;
}
