package com.utree.eightysix.request;

import com.utree.eightysix.rest.Param;

/**
 */
public class PaginateRequest {

    @Param("currPage")
    public int currPage;

    public PaginateRequest(int currPage) {
        this.currPage = currPage;
    }
}
