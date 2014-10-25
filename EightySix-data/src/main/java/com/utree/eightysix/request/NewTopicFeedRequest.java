package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_TOPIC_NEW)
@Cache
@Token
public class NewTopicFeedRequest extends Paginate {

  @Param("id")
  public String id;

  public NewTopicFeedRequest(String id, int currPage) {
    super(currPage);
    this.id = id;
  }
}
