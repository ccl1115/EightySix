package com.utree.eightysix.report;

import com.tencent.open.TaskGuide;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.utils.IOUtils;

import java.io.*;
import java.util.Calendar;
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

        if (BuildConfig.DEBUG) {
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
        } else {
          Calendar instance = Calendar.getInstance();
          File file = IOUtils.createTmpFile(instance.getTime().toString() + "-" + ex.toString());

          PrintWriter writer = null;
          FileWriter wr = null;
          try {
            wr = new FileWriter(file);
            writer = new PrintWriter(wr);
            ex.printStackTrace(writer);
          } catch (IOException ignored) {
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
        }

        android.os.Process.killProcess(android.os.Process.myPid());
      }
    });
  }


  @Override
  public void reportRequestError(RequestData requestData, Throwable t) {
    Properties properties = fromRequestData(requestData);
    properties.setProperty("throwable", t.getMessage() == null ? t.toString() : t.getMessage());
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
