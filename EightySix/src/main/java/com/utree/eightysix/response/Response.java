package com.utree.eightysix.response;

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
