package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.response.data.Comment;
import com.utree.eightysix.rest.Response;

/**
 */
public class CommentResponse extends Response {

    @SerializedName("object")
    public Comment object;
}
