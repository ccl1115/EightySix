package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_TOPIC_LIST)
@Cache
@Token
public class TopicListRequest extends Paginate {

  public TopicListRequest(int currPage) {
    super(currPage);
  }
}
