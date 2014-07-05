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

  @Param ("factoryID")
  public int factoryId;

  @Param ("postId")
  public String postId;

  public PostCommentsRequest(int factoryId, String postId, int page) {
    super(page);
    this.factoryId = factoryId;
    this.postId = postId;
  }
}
