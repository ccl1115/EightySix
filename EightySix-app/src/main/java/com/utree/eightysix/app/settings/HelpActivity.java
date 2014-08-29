package com.utree.eightysix.app.settings;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author simon
 */
@Layout(R.layout.activity_help)
@TopTitle(R.string.help)
public class HelpActivity extends BaseActivity {

  @InjectView(R.id.tv_help)
  TextView mTvHelp;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      InputStream inputStream = getAssets().open("help.txt");
      byte[] buffer = new byte[inputStream.available()];
      inputStream.read(buffer);

      mTvHelp.setText(Html.fromHtml(new String(buffer)));
    } catch (IOException ignored) {
    }
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }
}