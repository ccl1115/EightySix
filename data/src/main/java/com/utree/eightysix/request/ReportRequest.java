package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Optional;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_USER_REPORT)
@Token
public class ReportRequest {

  public static final int TYPE_1 = 1;
  public static final int TYPE_2 = 2;
  public static final int TYPE_3 = 3;
  public static final int TYPE_4 = 4;
  public static final int TYPE_5 = 5;
  public static final int TYPE_6 = 6;

  @Param("ownerPostId")
  public String postId;

  @Param("postId")
  @Optional
  public String commentId;

  @Param("type")
  public int type;

  @Param("content")
  @Optional
  public String content;

  public ReportRequest(int type, String postId, String commentId) {
    this.type = type;
    this.commentId = commentId;
    this.postId = postId;
  }

  public ReportRequest(int type, String postId) {
    this.type = type;
    this.postId = postId;
  }
}
