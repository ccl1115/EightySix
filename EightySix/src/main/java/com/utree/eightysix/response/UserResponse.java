package com.utree.eightysix.response;


import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.User;
import com.utree.eightysix.rest.Response;

/**
 */
public class UserResponse extends Response {

    @SerializedName("object")
    public User object;
}
