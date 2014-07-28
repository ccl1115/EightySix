package com.utree.eightysix.app.settings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.MD5Util;
import java.io.File;

/**
 * @author simon
 */
public class UpgradeService extends Service {

  private static final int NOTIFICATION_ID = 0x9000;

  private NotificationCompat.Builder mBuilder;
  private NotificationManager mNotificationManager;

  public static void start(Context context, Sync.Upgrade upgrade) {
    Intent intent = new Intent(context, UpgradeService.class);
    intent.putExtra("upgrade", upgrade);
    context.startService(intent);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    Sync.Upgrade upgrade = intent.getParcelableExtra("upgrade");

    if (upgrade == null) {
      stopSelf();
      return START_NOT_STICKY;
    }

    mBuilder = new NotificationCompat.Builder(this);
    mBuilder.setTicker("开始下载新版本")
        .setProgress(100, 0, false)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_icon))
        .setSmallIcon(R.drawable.notif_icon);


    File tmpFile = IOUtils.createTmpFile(String.format("upgrade_%s.apk", upgrade.version));
    if (tmpFile.exists() && upgrade.md5 != null && upgrade.md5.toLowerCase().equals(MD5Util.getMD5(tmpFile).toLowerCase())) {
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setDataAndType(Uri.fromFile(tmpFile), "application/vnd.android.package-archive");
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(i);
    } else {
      if (tmpFile.exists()) tmpFile.delete();
      U.getRESTRequester().getClient()
          .get(this, upgrade.url, null, new FileAsyncHttpResponseHandler(tmpFile) {

            @Override
            public void onStart() {
              mBuilder.setContentTitle("开始下载");
              mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
              updateNotification((int) (100 * (bytesWritten / (float) totalSize)),
                  bytesWritten / 1024, totalSize / 1024);

            }

            @Override
            public void onSuccess(File file) {
              Intent i = new Intent(Intent.ACTION_VIEW);
              i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
              i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(i);
            }

            @Override
            public void onFailure(Throwable e, File response) {
              mBuilder.setContentText("下载完成");
              mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
          });
    }
    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void updateNotification(int percent, int downloadedKb, int totalKb) {
    mBuilder.setContentText(String.format("下载中：%d Kb / %d Kb", downloadedKb, totalKb));
    mBuilder.setProgress(100, percent, false);
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
  }

}
