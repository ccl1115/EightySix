package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;
import com.utree.eightysix.rest.Log;

/**
 * @author simon
 */
@Api (C.API_COMMENT_LIST)
@Token
@Cache
@Log
public class PostCommentsRequest extends Paginate {

  @Param ("postId")
  public String postId;

  @Param ("viewType")
  public int viewType;

  @Param ("isHot")
  public int isHot;

  @Param("isRepost")
  public int isRepost;

  public PostCommentsRequest(String postId, int viewType, int isHot, int isRepost, int page) {
    super(page);
    this.viewType = viewType;
    this.postId = postId;
    this.isHot = isHot;
    this.isRepost = isRepost;
  }
}
