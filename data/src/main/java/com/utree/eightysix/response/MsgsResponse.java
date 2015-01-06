package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Msgs;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class MsgsResponse extends Response {

  @SerializedName("object")
  public Msgs object;
}
