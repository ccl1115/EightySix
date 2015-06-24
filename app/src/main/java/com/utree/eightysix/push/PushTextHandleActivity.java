package com.utree.eightysix.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.utree.eightysix.data.AppIntent;
import com.utree.eightysix.utils.CmdHandler;

/**
 */
public class PushTextHandleActivity extends Activity {

  public static Intent getIntent(Context context, AppIntent appIntent) {
    Intent intent = new Intent(context, PushTextHandleActivity.class);
    intent.putExtra("intent", appIntent);
    intent.setAction(appIntent.cmd);
    return intent;
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    AppIntent appIntent = getIntent().getParcelableExtra("intent");
    if (appIntent != null) {
      CmdHandler.inst().handle(this, appIntent.cmd);
    }
    finish();
  }

}
