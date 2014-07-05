package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.PostComments;
import com.utree.eightysix.rest.Response;

/**
 */
public class PostCommentsResponse extends Response {

    @SerializedName("object")
    public PostComments object;
}
