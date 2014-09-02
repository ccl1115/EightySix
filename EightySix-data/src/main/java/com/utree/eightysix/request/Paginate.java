package com.utree.eightysix.request;

import com.utree.eightysix.rest.Param;

/**
 */
public class Paginate {

    @Param("currPage")
    public int currPage;

    public Paginate(int currPage) {
        this.currPage = currPage;
    }
}
