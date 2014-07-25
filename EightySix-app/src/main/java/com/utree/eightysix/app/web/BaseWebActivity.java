package com.utree.eightysix.app.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.share.ShareDialog;
import com.utree.eightysix.drawable.GearsDrawable;
import com.utree.eightysix.widget.GearsView;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
@Layout(R.layout.activity_base_web)
public class BaseWebActivity extends BaseActivity {


  private WebViewClient mWebViewClient;

  @InjectView(R.id.wb_base)
  public WebView mWbBase;

  @InjectView(R.id.gv_loading)
  public GearsView mGvLoading;

  @InjectView(R.id.ll_error)
  public LinearLayout mLlError;

  public static void start(Context context, String url) {
    Intent intent = new Intent(context, BaseWebActivity.class);
    intent.putExtra("url", url);
    context.startActivity(intent);
  }

  public static void start(Context context, String title, String url) {
    Intent intent = new Intent(context, BaseWebActivity.class);
    intent.putExtra("title", title);
    intent.putExtra("url", url);
    context.startActivity(intent);
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String title = getIntent().getStringExtra("title");
    if (title != null) {
      setTopTitle(title);
    }


    mWebViewClient = new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mGvLoading.setVisibility(View.VISIBLE);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mGvLoading.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        mWbBase.setVisibility(View.INVISIBLE);
        mLlError.setVisibility(View.VISIBLE);
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("BaseWebActivity", "overriding url loading: " + url);
        if (url.contains("?")) {
          String[] str = url.split("\\?")[1].split("&");
          for (String s : str) {
            String[] kv = s.split("=");
            if (kv.length == 2) {
              if (kv[0].equals("factoryId")) {
                U.getShareManager().shareAppDialog(BaseWebActivity.this, Integer.parseInt(kv[1])).show();
                return true;
              }
            }
          }
        }
        return super.shouldOverrideUrlLoading(view, url);
      }
    };

    mWbBase.setWebViewClient(mWebViewClient);
    mWbBase.loadUrl(getIntent().getStringExtra("url"));
  }


}
