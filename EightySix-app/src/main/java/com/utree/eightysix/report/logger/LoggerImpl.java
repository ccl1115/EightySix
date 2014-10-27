package com.utree.eightysix.report.logger;

import android.os.AsyncTask;
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

        String[] columns = new String[entryAdapter.getPayload().size()];
        entryAdapter.getPayload().keySet().toArray(columns);
        builder.columns(columns);

        String[] values = new String[entryAdapter.getPayload().size()];
        entryAdapter.getPayload().values().toArray(values);
        builder.values(values);

        mInfluxDB.write("app-logger", TimeUnit.MILLISECONDS, builder.build());
        return null;
      }
    }).execute();
  }


}
