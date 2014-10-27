package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 */
public class Topics {

  @SerializedName("newTopic")
  public TopicGroup newTopic;

  @SerializedName("hotTopic")
  public TopicGroup hotTopic;

  public static class TopicGroup {

    @SerializedName("headTitle")
    public String headTitle;

    @SerializedName("postTopics")
    public Paginate<Topic> postTopics;
  }
}
