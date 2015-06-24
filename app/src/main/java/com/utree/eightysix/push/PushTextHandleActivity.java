package com.utree.eightysix.push;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.AppIntent;
import com.utree.eightysix.utils.CmdHandler;

/**
 */
public class PushTextHandleActivity extends Activity {

  public static Intent getIntent(Context context, AppIntent appIntent) {
    Intent intent = new Intent(context, PushTextHandleActivity.class);
    intent.putExtra("intent", appIntent);
    context.startActivity(intent);

    intent.setAction(appIntent.cmd);

    return intent;
  }

  @Override
  public void onCreate(Bundle bundle) {
    AppIntent appIntent = (AppIntent) getIntent().getSerializableExtra("intent");
    CmdHandler.inst().handle(this, appIntent.cmd);
    finish();
  }

}
