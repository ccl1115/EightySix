package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author simon
 */
public class PostComments {

  @SerializedName("postView")
  public Post post;

  @SerializedName("comments")
  public Comments comments;

  @SerializedName("blueStar")
  public int blueStar;

  @SerializedName("blueStarType")
  public int blueStarType;

  @SerializedName("starToken")
  public String starToken;

  public static class Comments {

    @SerializedName("lists")
    public List<Comment> lists;
  }
}
