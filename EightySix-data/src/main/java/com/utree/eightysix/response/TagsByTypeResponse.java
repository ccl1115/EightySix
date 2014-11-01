package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.rest.Response;
import java.util.List;

/**
 */
public class TagsByTypeResponse extends Response {

  @SerializedName ("object")
  public TagsByType object;

  public static class TagsByType {
    @SerializedName("lists")
    public List<TypedTags> lists;

    @SerializedName("topicView")
    public Topic mTopic;
  }

  public static class TypedTags {
    @SerializedName("id")
    public int id;

    @SerializedName("typeName")
    public String typeName;

    @SerializedName("tags")
    public List<Tag> tags;
  }
}
