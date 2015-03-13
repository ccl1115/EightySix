package com.utree.eightysix.app.settings;

import android.os.Bundle;
import android.webkit.WebView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;

import java.io.*;

/**
 * @author simon
 */
@Layout(R.layout.activity_help)
@TopTitle(R.string.help)
public class HelpActivity extends BaseActivity {

  @InjectView(R.id.content)
  WebView mWvHelp;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      InputStream inputStream = getAssets().open("help.html");
      Reader reader = new InputStreamReader(inputStream);
      char[] buffer = new char[inputStream.available()];
      reader.read(buffer);
      StringWriter sw = new StringWriter();
      sw.write(buffer);

      mWvHelp.loadDataWithBaseURL(null, sw.toString(), "text/html", "utf-8", null);
    } catch (IOException ignored) {
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }
}