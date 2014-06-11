package com.utree.eightysix.test;

import android.util.Log;
import com.loopj.android.http.TextHttpResponseHandler;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.request.LoginRequest;
import java.util.concurrent.CountDownLatch;
import org.apache.http.Header;

/**
 */
public class RequesterTestCase extends BaseInstrumentationTestCase {


    public void testLoginRequest() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                U.getRESTRequester().request(new LoginRequest("18688716376", "test-test-test"),
                        new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(String responseBody, Throwable error) {
                                super.onFailure(responseBody, error);
                                Log.d(C.TAG.RR, responseBody);
                                signal.countDown();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                                super.onSuccess(statusCode, headers, responseBody);
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }
}
