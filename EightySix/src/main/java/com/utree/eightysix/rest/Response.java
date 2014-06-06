package com.utree.eightysix.rest;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Response<T> {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("object")
    public T object;
}
