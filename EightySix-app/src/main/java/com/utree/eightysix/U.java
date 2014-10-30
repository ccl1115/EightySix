package com.utree.eightysix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.disklrucache.DiskLruCache;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.app.SyncClient;
import com.utree.eightysix.app.feed.BaseItemDeserializer;
import com.utree.eightysix.applogger.EntryLogger;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.push.PushHelper;
import com.utree.eightysix.push.XGPushHelper;
import com.utree.eightysix.qrcode.ActionDispatcher;
import com.utree.eightysix.qrcode.actions.AddFriendAction;
import com.utree.eightysix.applogger.LoggerImpl;
import com.utree.eightysix.rest.IRESTRequester;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.app.share.ShareManager;
import com.utree.eightysix.statistics.Analyser;
import com.utree.eightysix.statistics.MtaAnalyserImpl;
import com.utree.eightysix.storage.Storage;
import com.utree.eightysix.storage.oss.OSSImpl;
import com.utree.eightysix.utils.CacheUtils;
import com.utree.eightysix.report.Reporter;
import com.utree.eightysix.report.ReporterImpl;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Most helpful methods and singleton instances
 * <p/>
 * <b>Most of these methods must be invoked in main thread.</b>
 */
public class U {

  public static final int SECOND_IN_MS = 1000;
  public static final int DAY_IN_MS = 86400000; // 60 * 60 * 24 * 1000
  public static final int HOUR_IN_MS = 3600000; // 60 * 60 * 1000
  public static final int MINUTE_IN_MS = 60 * 1000;

  private static Analyser sStatistics;
  private static Storage sCloudStorage;
  private static IRESTRequester sRESTRequester;
  private static CacheUtils sCacheUtils;
  private static Reporter sReporter;
  private static ShareManager sShareManager;
  private static PushHelper sPushHelper;
  private static Gson sGson ;
  private static Properties sConfiguration;
  private static Fixture sFixture;
  private static Toast sToast;
  private static SyncClient sSyncClient;
  private static EntryLogger sEntryLogger;

  private static final Object lock = new Object();

  private static Bus sBus;
  private static Bus sChatBus;

  public static ShareManager getShareManager() {
    if (sShareManager == null) {
      synchronized (lock) {
        sShareManager = new ShareManager();
      }
    }
    return sShareManager;
  }

  public static SyncClient getSyncClient() {
    if (sSyncClient == null) {
      sSyncClient = new SyncClient();
    }
    return sSyncClient;
  }

  public static Reporter getReporter() {
    if (sReporter == null) {
      synchronized (lock) {
        sReporter = new ReporterImpl();
      }
    }
    return sReporter;
  }

  public static Gson getGson() {
    if (sGson == null) {
      synchronized (lock) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(BaseItem.class, new BaseItemDeserializer());
        sGson = builder.create();
      }
    }
    return sGson;
  }

  public static Analyser getAnalyser() {
    if (sStatistics == null) {
      synchronized (lock) {
        sStatistics = new MtaAnalyserImpl();
      }
    }
    return sStatistics;
  }

  public static Context getContext() {
    return BaseApplication.getContext();
  }

  public static Storage getCloudStorage() {
    if (sCloudStorage == null) {
      sCloudStorage = new OSSImpl();
    }
    return sCloudStorage;
  }

  public static void viewBinding(View view, Object target) {
    ButterKnife.inject(target, view);
  }

  public static IRESTRequester getRESTRequester() {
    M.checkThread();
    if (sRESTRequester == null) {
      sRESTRequester = new RESTRequester(getConfig("api.host"));
    }
    return sRESTRequester;
  }

  private static CacheUtils getCacheUtils() {
    if (sCacheUtils == null) {
      sCacheUtils = CacheUtils.inst();
    }
    return sCacheUtils;
  }

  public static PushHelper getPushHelper() {
    M.checkThread();
    if (sPushHelper == null) {
      sPushHelper = new XGPushHelper();
    }
    return sPushHelper;
  }

  public static DiskLruCache getApiCache() {
    return getCacheUtils().getCache(U.getConfig("cache.api.dir"),
        U.getConfigInt("cache.api.version"),
        U.getConfigInt("cache.api.count"),
        U.getConfigLong("cache.api.size"));
  }

  public static DiskLruCache getContactsCache() {
    return getCacheUtils().getCache(U.getConfig("cache.contacts.dir"),
        U.getConfigInt("cache.contacts.version"),
        U.getConfigInt("cache.contacts.count"),
        U.getConfigLong("cache.contacts.size"));
  }

  public static DiskLruCache getImageCache() {
    return getCacheUtils().getCache(U.getConfig("cache.image.dir"),
        U.getConfigInt("cache.image.version"),
        U.getConfigInt("cache.image.count"),
        U.getConfigInt("cache.image.size"));
  }

  public static String getConfig(String key) {
    if (sConfiguration == null) {
      loadConfig();
    }
    return sConfiguration.getProperty(key);
  }

  public static String getImageBucket() {
    if (BuildConfig.DEBUG) {
      return U.getConfig("storage.image.bucket.name");
    } else {
      return U.getConfig("storage.image.bucket.name.release");
    }
  }

  public static String getBgBucket() {
    if (BuildConfig.DEBUG) {
      return U.getConfig("storage.bg.bucket.name");
    } else {
      return U.getConfig("storage.bg.bucket.name.release");
    }
  }

  @SuppressLint("SdCardPath")
  private static void loadConfig() {
    M.checkThread();
    sConfiguration = new Properties();
    if (!BuildConfig.DEBUG) {
      // in release version we only load internal configuration file.
      loadInternalConfig();
      return;
    }
    File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/configuration.properties");
    if (!file.exists()) {
      file = new File("/sdcard/configuration.properties");
    }
    if (file.exists() && file.isFile()) {
      FileReader in = null;
      try {
        in = new FileReader(file);
        sConfiguration.load(in);
      } catch (Exception e) {
        loadInternalConfig();
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException ignored) {
          }
        }
      }
    } else {
      loadInternalConfig();
    }
  }

  private static void loadInternalConfig() {
    try {
      sConfiguration.load(U.getContext().getResources().openRawResource(R.raw.configuration));
    } catch (IOException ignored) {
    }
  }

  public static int getConfigInt(String key) {
    try {
      return Integer.parseInt(getConfig(key));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public static long getConfigLong(String key) {
    try {
      return Long.parseLong(getConfig(key));
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  public static boolean getConfigBoolean(String key) {
    return Boolean.parseBoolean(getConfig(key));
  }

  public static Bus getBus() {
    M.checkThread();
    if (sBus == null) {
      sBus = new Bus(ThreadEnforcer.MAIN);
    }
    return sBus;
  }

  public static Bus getChatBus() {
    M.checkThread();
    if (sChatBus == null) {
      sChatBus = new Bus(ThreadEnforcer.MAIN, "chat");
    }
    return sChatBus;
  }

  private static HashMap<String, Bus> sPrivateBuses = new HashMap<String, Bus>();

  public static Bus getBus(String channel) {
    M.checkThread();

    Bus b = sPrivateBuses.get(channel);

    if (b != null) {
      return b;
    } else {
      b = new Bus(channel);
      sPrivateBuses.put(channel, b);
      return b;
    }
  }

  public static int dp2px(int dp) {
    return (int) (U.getContext().getResources().getDisplayMetrics().density * dp + 0.5f);
  }


  public static String timestamp(long timestamp) {
    final long now = new Date().getTime();
    final long t = now - timestamp;
    if (t < 0) {
      return "刚刚";
    } else if (t < MINUTE_IN_MS) {
      return (t / SECOND_IN_MS) + "秒前";
    } else if (t < HOUR_IN_MS) {
      return (t / MINUTE_IN_MS) + "分钟前";
    } else if (t < DAY_IN_MS) {
      return (t / HOUR_IN_MS) + "小时前";
    } else {
      return (t / DAY_IN_MS) + "天前";
    }
  }

  /**
   * getString() when you don't have a context
   *
   * @param id the string id
   * @return the string
   */
  public static String gs(int id) {
    return U.getContext().getString(id);
  }

  /**
   * get formatted string
   *
   * @param id      the string id
   * @param objects the populated values
   * @return the formatted string
   */
  public static String gfs(int id, Object... objects) {
    return String.format(U.getContext().getString(id), objects);
  }

  public static Drawable gd(int id) {
    return U.getContext().getResources().getDrawable(id);
  }

  /**
   * Easily show a un-managed toast
   *
   * @param string the string to show
   */
  public static void showToast(String string) {
    if (sToast != null) {
      sToast.cancel();
    }
    sToast = Toast.makeText(getContext(), string, Toast.LENGTH_SHORT);
    sToast.show();
  }

  private static ActionDispatcher sActionDispatcher;

  public static ActionDispatcher getQRCodeActionDispatcher(){
    M.checkThread();

    if (sActionDispatcher == null) {
      sActionDispatcher = new ActionDispatcher();
      sActionDispatcher.register(new AddFriendAction());
    }

    return sActionDispatcher;
  }

  public static EntryLogger getAppLogger() {
    M.checkThread();

    if (sEntryLogger == null) {
      sEntryLogger = new LoggerImpl(U.getConfig("app.logger.host"));
    }

    return sEntryLogger;
  }

  public static boolean useFixture() {
    return getConfigBoolean("debug.fixture");
  }

  public static <T> List<T> getFixture(Class<T> clz, int quantity, String template) {
    return sFixture == null ? null : sFixture.get(clz, quantity, template);
  }

  static {
    try {
      Class fixtureClass = Class.forName("com.utree.eightysix.fixture.FixtureImpl");
      sFixture = (Fixture) fixtureClass.newInstance();
    } catch (Exception e) {
      if (BuildConfig.DEBUG) e.printStackTrace();
    }
  }
}
