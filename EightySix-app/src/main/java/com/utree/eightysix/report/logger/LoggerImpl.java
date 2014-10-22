package com.utree.eightysix.report.logger;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.utree.eightysix.BuildConfig;
import de.akquinet.android.androlog.Log;
import java.io.UnsupportedEncodingException;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

/**
 */
public class LoggerImpl implements EntryLogger {

  private static final String TAG = "LoggerImpl";

  private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

  private Context mContext;

  private final String mHost;
  private AsyncHttpResponseHandler mResponseHandler = new AsyncHttpResponseHandler() {
    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
      Log.d(TAG, "log entry to server suc");
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
      if (BuildConfig.DEBUG) {
        throwable.printStackTrace();
      }
      Log.d(TAG, "log entry to server fail");
    }
  };

  public LoggerImpl(Context context, String host) {
    mContext = context;
    mHost = host;
  }

  @Override
  public <T extends EntryAdapter> void log(T entryAdapter) {
    try {
      StringEntity entity = new StringEntity(entryAdapter.getPayload().normalize(), "utf-8");
      entity.setContentType("application/json; charset=utf-8");
      mAsyncHttpClient.post(mContext, mHost + entryAdapter.getApi(),
          entity,
          null,
          mResponseHandler);
    } catch (UnsupportedEncodingException ignored) {
    }
  }


}
