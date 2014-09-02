package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;

/**
 * @author simon
 */
@Api(C.API_SYNC)
public class SyncRequest {

  @Param("parentId")
  public String parentId;

  public SyncRequest(String parentId) {
    this.parentId = parentId;
  }
}
