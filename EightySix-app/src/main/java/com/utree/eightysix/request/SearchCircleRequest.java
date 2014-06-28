package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api (C.API_FACTORY_SEARCH)
@Token
public class SearchCircleRequest extends Paginate {

    @Param ("keywords")
    public String keywords;

    public SearchCircleRequest(int currPage, String keywords) {
        super(currPage);
        this.keywords = keywords;
    }
}
