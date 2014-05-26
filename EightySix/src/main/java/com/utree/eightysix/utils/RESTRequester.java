package com.utree.eightysix.utils;

import android.os.Build;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import org.apache.http.Header;

/**
 */
public class RESTRequester {

    private AsyncHttpClient mAsyncHttpClient;

    private String mHost;

    public RESTRequester(String host) {
        mHost = host;
        mAsyncHttpClient = new AsyncHttpClient();
    }

    public RESTRequester(String host, int maxConnections) {
        this(host);
        mAsyncHttpClient.setMaxConnections(maxConnections);
    }

    public RESTRequester(String host, int maxConnections, int retry, int timeout) {
        this(host, maxConnections);
        mAsyncHttpClient.setMaxRetriesAndTimeout(retry, timeout);
    }

    public RequestHandle get(String api, Header[] headers, RequestParams params, ResponseHandlerInterface handler) {
        putBaseParams(params);
        return mAsyncHttpClient.get(U.getContext(), mHost + C.API.get(api), headers, params, handler);
    }

    public RequestHandle post(String api, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface handler) {
        putBaseParams(params);
        return mAsyncHttpClient.post(U.getContext(), mHost + C.API.get(api), headers, params, contentType, handler);
    }

    private void putBaseParams(RequestParams params) {
        if (params == null) {
            params = new RequestParams();
        }

        params.add("os", "android");
        params.add("os_version", String.valueOf(Build.VERSION.SDK_INT));
        params.add("device", Build.DEVICE);
        params.add("model", Build.MODEL);
        params.add("manufacturer", Build.MANUFACTURER);
        params.add("imei", C.IMEI);
        params.add("version", String.valueOf(C.VERSION));
        params.add("channel", U.getConfig(C.CONFIG_KEY.CHANNEL));

    }
}
