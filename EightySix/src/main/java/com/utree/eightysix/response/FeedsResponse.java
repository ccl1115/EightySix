package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Feeds;
import com.utree.eightysix.rest.Response;

/**
 */
public class FeedsResponse extends Response {

    @SerializedName("object")
    public Feeds object;

}
