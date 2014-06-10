package com.utree.eightysix.response.data;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.response.Paginate;

/**
 */
public class Circles {

    @SerializedName("factoryList")
    public Paginate<Circle> factoryCircle;
}
