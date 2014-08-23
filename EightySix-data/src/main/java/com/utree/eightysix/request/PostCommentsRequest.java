package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api (C.API_COMMENT_LIST)
@Token
@Cache
public class PostCommentsRequest extends Paginate {

  @Param ("postId")
  public String postId;


  @Param ("viewType")
  public int viewType;

  public PostCommentsRequest(String postId, int viewType, int page) {
    super(page);
    this.viewType = viewType;
    this.postId = postId;
  }
}
