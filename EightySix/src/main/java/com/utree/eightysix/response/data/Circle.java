package com.utree.eightysix.response.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Circle {

    @SerializedName("activeLevel")
    public int activeLevel;

    @SerializedName("cityName")
    public String cityName;

    @SerializedName("distance")
    public int distance;

    @SerializedName("factoryType")
    public int factoryType;

    @SerializedName("lock")
    public int lock;

    @SerializedName("viewSortType")
    public int viewSortType;

    @SerializedName("viewType")
    public int viewType;
}
