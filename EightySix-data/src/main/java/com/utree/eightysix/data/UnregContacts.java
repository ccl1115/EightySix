package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author simon
 */
public class UnregContacts {

  @SerializedName("unRegContacts")
  public List<UnregContact> unregContacts;

  public static class UnregContact {
    @SerializedName("phone")
    public String phone;

    @SerializedName("name")
    public String name;
  }
}
