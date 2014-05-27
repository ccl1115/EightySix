package com.utree.eightysix.test;

import android.util.Log;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.TextHttpResponseHandler;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.request.LoginRequest;
import com.utree.eightysix.request.RESTRequester;
import java.util.concurrent.CountDownLatch;
import org.apache.http.Header;

/**
 */
public class RequesterTestCase extends BaseInstrumentationTestCase {

    private RESTRequester mRESTRequester;
    private TextHttpResponseHandler mHandler;

    private CountDownLatch mSignal;

    public void testLoginRequest() throws Throwable {

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestHandle handle = mRESTRequester.request(
                        new LoginRequest("18688716376", "test-test-test"), mHandler);

                assertTrue(!handle.isCancelled());
                assertTrue(!handle.isFinished());
            }
        });

        mSignal.await();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mSignal = new CountDownLatch(1);
        mRESTRequester = U.getRESTRequester();
        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(String responseBody, Throwable error) {
                super.onFailure(responseBody, error);
                Log.d(C.TAG.AH, responseBody);
                mSignal.countDown();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                Log.d(C.TAG.AH, responseBody);
                mSignal.countDown();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mSignal.countDown();
            }
        };
    }
}
