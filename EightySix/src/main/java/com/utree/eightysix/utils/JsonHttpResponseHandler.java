package com.utree.eightysix.utils;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import org.apache.http.Header;

/**
 *
 * Extends from BaseJsonHttpResponseHandler<T> to make parseResponse() public
 */
public abstract class JsonHttpResponseHandler<T> extends BaseJsonHttpResponseHandler<T> {

    @Override
    public abstract void onSuccess(int statusCode, Header[] headers, String rawResponse, T response);

    @Override
    public abstract void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, T errorResponse);

    @Override
    public abstract T parseResponse(String responseBody) throws Throwable;
}
