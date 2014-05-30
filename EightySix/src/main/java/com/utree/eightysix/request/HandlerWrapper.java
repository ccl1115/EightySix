package com.utree.eightysix.request;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.response.OnResponse;
import com.utree.eightysix.response.Response;
import de.akquinet.android.androlog.Log;
import java.io.IOException;

/**
 * Wrapper of handler use to parse response data using Gson and cache automatically
 */
public class HandlerWrapper<T> extends BaseJsonHttpResponseHandler<Response<T>> {

    private String mKey;
    private OnResponse<Response<T>> mOnResponse;

    /**
     * No cache constructor
     * @param onResponse the callback
     */
    public HandlerWrapper(OnResponse<Response<T>> onResponse) {
        mOnResponse = onResponse;
    }

    /**
     * Cache response uing the key
     * @param key the cache key
     * @param onResponse the callback
     */
    public HandlerWrapper(String key, OnResponse<Response<T>> onResponse) {
        mKey = key;
        mOnResponse = onResponse;
    }

    @Override
    public void onSuccess(int statusCode, org.apache.http.Header[] headers, String rawResponse, Response<T> response) {
        if (response != null) {
            if (mKey != null) {
                try {
                    U.getApiCache().edit(mKey).set(0, rawResponse);
                } catch (IOException e) {
                    U.getAnalyser().reportException(U.getContext(), e);
                }
            }
            mOnResponse.onResponse(response);
        } else {
            mOnResponse.onResponse(null);
        }
    }

    @Override
    public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, String rawData, Response<T> errorResponse) {
        mOnResponse.onResponse(null);
    }

    @Override
    public Response<T> parseResponse(String responseBody) throws Throwable {
        Log.d(C.TAG.RR, "response: " + responseBody);
        return U.getGson().fromJson(responseBody, new TypeToken<Response<T>>() {
        }.getType());
    }
}
