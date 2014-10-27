package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_TOPIC_FEATURE)
@Cache
@Token
public class FeatureTopicFeedRequest extends Paginate {

  @Param("id")
  public String id;

  public FeatureTopicFeedRequest(String id, int currPage) {
    super(currPage);
    this.id = id;
  }
}
