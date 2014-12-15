package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.UnregContacts;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class UnregContactsResponse extends Response {

  @SerializedName("object")
  public UnregContacts object;
}
