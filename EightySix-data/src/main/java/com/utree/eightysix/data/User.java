package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
* Created by simon on 14-6-7.
*/
public class User {
    @SerializedName("userId")
    public String userId;

    @SerializedName("token")
    public String token;
}
