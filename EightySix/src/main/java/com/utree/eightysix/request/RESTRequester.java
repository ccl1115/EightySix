package com.utree.eightysix.request;

import android.os.Build;
import com.baidu.android.common.util.CommonParam;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.Env;
import de.akquinet.android.androlog.Log;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 */
public class RESTRequester {

    private AsyncHttpClient mAsyncHttpClient;

    private String mHost;

    public RESTRequester(String host) {
        mHost = host;
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.setMaxConnections(U.getConfigInt("api.connections"));
        mAsyncHttpClient.setMaxRetriesAndTimeout(U.getConfigInt("api.retry"), U.getConfigInt("api.timeout"));
    }

    public RESTRequester(String host, int maxConnections) {
        mHost = host;
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.setMaxConnections(maxConnections);
        mAsyncHttpClient.setMaxRetriesAndTimeout(U.getConfigInt("api.retry"), U.getConfigInt("api.timeout"));
    }

    public RESTRequester(String host, int maxConnections, int retry, int timeout) {
        mHost = host;
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.setMaxConnections(maxConnections);
        mAsyncHttpClient.setMaxRetriesAndTimeout(retry, timeout);
    }

    private RequestHandle get(String api, Header[] headers, RequestParams params, ResponseHandlerInterface handler) {
        Log.d(C.TAG.AH, "   get: " + mHost + api);
        Log.d(C.TAG.AH, "params: " + params.toString());
        putBaseParams(params);
        return mAsyncHttpClient.get(U.getContext(), mHost + api, headers, params, handler);
    }

    private RequestHandle post(String api, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface handler) {
        Log.d(C.TAG.AH, "  post: " + mHost + api);
        Log.d(C.TAG.AH, "params: " + params.toString());
        putBaseParams(params);
        return mAsyncHttpClient.post(U.getContext(), mHost + api, headers, params, contentType, handler);
    }

    public RequestHandle request(Object request, ResponseHandlerInterface handler) {
        RequestData data = convert(request);
        return request(data, handler);
    }

    public RequestHandle request(RequestData data, ResponseHandlerInterface handler) {
        if (data.method == Method.METHOD.GET) {
            return get(data.api, data.headers, data.params, handler);
        } else if (data.method == Method.METHOD.POST) {
            return post(data.api, data.headers, data.params, null, handler);
        }
        return null;
    }

    public static class RequestData {
        Method.METHOD method;
        public String api;
        Header[] headers;
        public RequestParams params;
    }

    public RequestData convert(Object request) {
        RequestData data = new RequestData();
        Class<?> clz = request.getClass();

        List<Header> headers = new ArrayList<Header>();

        try {
            data.api = clz.getAnnotation(Api.class).value();
            data.params = new RequestParams();

            Token token = clz.getAnnotation(Token.class);

            if (token != null && Account.inst().isLogin()) {
                data.params.add("token", Account.inst().getToken());
                data.params.add("userId", Account.inst().getUserId());
            }

            Method method = clz.getAnnotation(Method.class);
            if (method != null) {
                data.method = method.value();
            } else {
                data.method = Method.METHOD.GET;
            }

            for (Field f : clz.getDeclaredFields()) {
                Param p = f.getAnnotation(Param.class);

                if (p != null) {
                    data.params.put(p.value(), f.get(request));
                }

                com.utree.eightysix.request.Header h = f.getAnnotation(com.utree.eightysix.request.Header.class);

                if (h != null) {
                    headers.add(new BasicHeader(h.value(), (String) f.get(request)));
                }
            }

            if (headers.size() > 0) {
                data.headers = new Header[headers.size()];
                headers.toArray(data.headers);
            }
        } catch (Throwable t) {
            U.getAnalyser().reportException(U.getContext(), t);
            throw new IllegalArgumentException("Request object parse failed");
        }

        return data;
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
        params.add("imei", Env.getImei());
        params.add("version", String.valueOf(C.VERSION));
        params.add("channel", U.getConfig("app.channel"));

        params.add("cuid", CommonParam.getCUID(U.getContext()));

        String pushChannelId = Env.getPushChannelId();
        if (pushChannelId != null) {
            params.add("push_channelid", pushChannelId);
        }

        String pushUserId = Env.getPushUserId();
        if (pushUserId != null) {
            params.add("push_userid", pushUserId);
        }
    }
}
