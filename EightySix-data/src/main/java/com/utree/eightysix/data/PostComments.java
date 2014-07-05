package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class PostComments {

  @SerializedName("postView")
  public Post post;

  @SerializedName("comments")
  public Paginate<Comment> comments;

}
