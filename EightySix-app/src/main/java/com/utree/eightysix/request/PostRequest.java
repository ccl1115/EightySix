package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api (C.API_POST)
@Token
public class PostRequest {

    @Param ("bgUrl")
    public String bgUrl;

    @Param ("bgColor")
    public String bgColor;

    @Param ("content")
    public String content;

    @Param ("factoryId")
    public int factoryId;

    public PostRequest(int factoryId, String content, String bgColor, String bgUrl) {
        this.factoryId = factoryId;
        this.content = content;
        this.bgColor = bgColor;
        this.bgUrl = bgUrl;
    }

}
