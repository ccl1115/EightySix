/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.publish;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.TypedValue;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.panel.Item;
import com.utree.eightysix.widget.panel.Page;
import com.utree.eightysix.widget.panel.Panel;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

/**
 */
public class BgSyncService extends Service {

  private SharedPreferences sp;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    XmlPullParser parser;
    sp = getSharedPreferences("bg_sync", MODE_PRIVATE);
    try {
      parser = XmlPullParserFactory.newInstance().newPullParser();
      parser.setInput(getResources().openRawResource(R.raw.publish_panel), "UTF-8");
      Panel panel = new Panel(parser);

      for (Page page : panel.getPages()) {
        for (Item item : page.getItems()) {
          if (item.getValue().type == TypedValue.TYPE_STRING) {
            CharSequence url = item.getValue().string;
            String urlHash = ImageUtils.getUrlHash(url.toString());
            if (!sp.getBoolean(urlHash, false)) {
              ImageUtils.asyncLoad(url.toString(),
                  urlHash);
            }
          }
        }
      }
    } catch (XmlPullParserException ignored) {
    } catch (IOException ignored) {
    }
    return START_NOT_STICKY;
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    sp.edit().putBoolean(event.getHash(), true).apply();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    M.getRegisterHelper().register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    M.getRegisterHelper().unregister(this);
  }
}
