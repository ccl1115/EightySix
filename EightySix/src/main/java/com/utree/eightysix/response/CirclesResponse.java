package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.response.data.Circles;
import com.utree.eightysix.rest.Response;

/**
 */
public class CirclesResponse extends Response {

    @SerializedName("object")
    public Circles object;
}
