package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Comment {

    @SerializedName("content")
    public String content;

    @SerializedName("countPraise")
    public int praise;

    @SerializedName("createTime")
    public long timestamp;

    @SerializedName("id")
    public int id;

    @SerializedName("userAvatar")
    public String avatar;
}
