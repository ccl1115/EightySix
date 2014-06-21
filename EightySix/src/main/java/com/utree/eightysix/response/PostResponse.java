package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.rest.Response;

/**
 */
public class PostResponse extends Response {

    @SerializedName("object")
    public Post object;

}
