package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.rest.Response;
import java.util.List;

/**
 */
public class TagsResponse extends Response {

  @SerializedName("object")
  public Tags object;

  public static class Tags {

    @SerializedName("tags")
    public List<Tag> tags;

    @SerializedName("lastTempName")
    public String lastTempName;
  }
}
