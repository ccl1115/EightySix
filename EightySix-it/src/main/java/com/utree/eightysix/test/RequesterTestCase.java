package com.utree.eightysix.test;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.util.Log;
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mRESTRequester = U.getRESTRequester();
    }

    public void testLoginRequest() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRESTRequester.request(new LoginRequest("18688716376", "test-test-test"), new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                        super.onSuccess(statusCode, headers, responseBody);
                        Log.d(C.TAG.RR, responseBody);
                        signal.countDown();
                    }

                    @Override
                    public void onFailure(String responseBody, Throwable error) {
                        super.onFailure(responseBody, error);
                        Log.d(C.TAG.RR, responseBody);
                        signal.countDown();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        signal.countDown();
                    }
                });
            }
        });

        signal.await();
    }
}
