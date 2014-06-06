package com.utree.eightysix.request;

import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.disklrucache.DiskLruCache;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.response.OnResponse;
import com.utree.eightysix.response.Response;
import de.akquinet.android.androlog.Log;
import java.io.IOException;
import java.lang.reflect.Type;
import org.apache.http.HttpStatus;

/**
 * Wrapper of handler use to parse response data using Gson and cache automatically
 * <p/>
 * And it will do error handling and cache automation.
 */
public class HandlerWrapper<T> extends BaseJsonHttpResponseHandler<Response<T>> {

    private String mKey;
    private OnResponse<Response<T>> mOnResponse;
    private Object mRequest;
    private Type mType;

    /**
     * No cache constructor
     *
     * @param request    the object represents the reqeust
     * @param onResponse the callback
     */
    public HandlerWrapper(Object request, OnResponse<Response<T>> onResponse, Type type) {
        mOnResponse = onResponse;
        mRequest = request;
        mType = type;
    }

    /**
     * Cache response using the key
     *
     * @param key        the cache key
     * @param request    the object represents the request
     * @param onResponse the callback
     */
    public HandlerWrapper(String key, Object request, OnResponse<Response<T>> onResponse, Type type) {
        mKey = key;
        mOnResponse = onResponse;
        mRequest = request;
        mType = type;
    }

    @Override
    public void onSuccess(int statusCode, org.apache.http.Header[] headers, String rawResponse, Response<T> response) {
        if (response != null) {
            errorHandle(response);

            Cache need = mRequest.getClass().getAnnotation(Cache.class);
            if (mKey != null && need != null) {
                try {
                    DiskLruCache.Editor edit = U.getApiCache().edit(mKey);
                    edit.set(0, rawResponse);
                    edit.commit();
                } catch (IOException e) {
                    U.getAnalyser().reportException(U.getContext(), e);
                }
            }
        }

        if (statusCode > HttpStatus.SC_MULTIPLE_CHOICES) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(U.getContext(), "HttpStatus: " + statusCode, Toast.LENGTH_SHORT).show();
            }
        }
        mOnResponse.onResponse(response);
    }

    @Override
    public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, String rawData, Response<T> errorResponse) {
        if (statusCode > HttpStatus.SC_MULTIPLE_CHOICES) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(U.getContext(), "HttpStatus: " + statusCode, Toast.LENGTH_SHORT).show();
            }
        }

        if (e != null) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(U.getContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        mOnResponse.onResponse(null);
    }

    @Override
    public Response<T> parseResponse(String responseBody) throws Throwable {
        Log.d(C.TAG.RR, "response: " + responseBody);
        return U.getGson().fromJson(responseBody, mType);
    }

    private void errorHandle(Response<T> response) {
        if (BuildConfig.DEBUG) {
            if (response.code != 0) {
                Toast.makeText(U.getContext(), String.format("%s(%d)", response.message, response.code), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
