package com.utree.eightysix.report;

import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.rest.RequestData;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

/**
 * @author simon
 */
public class ReporterImpl implements Reporter {

  public ReporterImpl() {
  }

  @Override
  public void init() {
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable ex) {
        reportAppCrash(ex);
        StringWriter wr = null;
        PrintWriter writer = null;
        try {
          wr = new StringWriter();
          writer = new PrintWriter(wr);
          ex.printStackTrace(writer);
          ReporterActivity.start(U.getContext(), wr.toString());
        } finally {
          if (writer != null) {
            writer.close();
          }

          if (wr != null) {
            try {
              wr.close();
            } catch (IOException ignored) {
            }
          }

        }

        System.exit(-1);
      }
    });
  }


  @Override
  public void reportRequestError(RequestData requestData, Throwable t) {
    Properties properties = fromRequestData(requestData);
    properties.setProperty("throwable", t.getMessage());
    U.getAnalyser().trackKVEvent(U.getContext(), "server_response_error", properties);
  }

  @Override
  public void reportRequestStatusCode(RequestData requestData, int statusCode) {
    Properties properties = fromRequestData(requestData);
    properties.setProperty("statusCode", String.valueOf(statusCode));
    U.getAnalyser().trackKVEvent(U.getContext(), "server_response_status", properties);
  }

  @Override
  public void reportAppCrash(Throwable t) {
    if (BuildConfig.DEBUG) {
      if (U.getConfigBoolean("report.debug")) {
        U.getAnalyser().reportException(U.getContext(), t);
      }
    } else {
      U.getAnalyser().reportException(U.getContext(), t);
    }
  }

  private Properties fromRequestData(RequestData data) {
    Properties properties = new Properties();
    properties.setProperty("api", data.getApi());
    properties.setProperty("params", data.getParams().toString());
    return properties;
  }
}