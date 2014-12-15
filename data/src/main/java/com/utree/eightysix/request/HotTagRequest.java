package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api (C.API_FEED_TAG_HOT)
@Token
@Cache
public class HotTagRequest extends Paginate {

  @Param ("tagId")
  public int tagId;

  public HotTagRequest(int currPage, int tagId) {
    super(currPage);
    this.tagId = tagId;
  }
}
