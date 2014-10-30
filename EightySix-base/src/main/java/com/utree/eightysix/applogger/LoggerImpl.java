package com.utree.eightysix.applogger;

import android.os.AsyncTask;
import com.utree.eightysix.BuildConfig;
import java.util.concurrent.TimeUnit;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;

/**
 */
public class LoggerImpl implements EntryLogger {

  private static final String TAG = "LoggerImpl";
  private InfluxDB mInfluxDB;

  public LoggerImpl(String host) {
    mInfluxDB = InfluxDBFactory.connect(host, "root", "root");
  }

  @Override
  public <T extends EntryAdapter> void log(final T entryAdapter) {
    (new AsyncTask<Void, Void, Void>() {

      @Override
      protected Void doInBackground(Void... voids) {
        Serie.Builder builder = new Serie.Builder(entryAdapter.getApi());

        Payload payload = entryAdapter.getPayload();

        String[] columns = new String[payload.size()];
        payload.keySet().toArray(columns);
        builder.columns(columns);

        Object[] values = new Object[payload.size()];
        payload.values().toArray(values);
        builder.values(values);

        try {
          mInfluxDB.write("app-logger", TimeUnit.MILLISECONDS, builder.build());
        } catch (Throwable t) {
          if (BuildConfig.DEBUG) {
            t.printStackTrace();
          }
        }
        return null;
      }
    }).execute();
  }


}
