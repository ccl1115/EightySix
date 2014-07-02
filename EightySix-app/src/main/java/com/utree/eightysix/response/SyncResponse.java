package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class SyncResponse extends Response {

  @SerializedName("object")
  public Sync object;
}
