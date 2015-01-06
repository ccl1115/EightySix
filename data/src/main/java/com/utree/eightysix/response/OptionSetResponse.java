package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.OptionSet;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class OptionSetResponse extends Response {

  @SerializedName("object")
  public OptionSet object;
}
