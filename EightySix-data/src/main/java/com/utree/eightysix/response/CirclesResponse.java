package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Circles;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.rest.Response;

/**
 */
public class CirclesResponse extends Response {

    @SerializedName("object")
    public Paginate<Circle> object;
}
