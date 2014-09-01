package com.utree.eightysix.app.circle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.event.LogoutListener;
import com.utree.eightysix.location.Location;
import com.utree.eightysix.request.CreateCircleRequest;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.widget.RoundedButton;
import java.io.File;
import java.util.regex.Pattern;

/**
 * @author simon
 */
@Layout (R.layout.activity_circle_create)
@TopTitle (R.string.create_circle)
public class CircleCreateActivity extends BaseActivity implements Location.OnResult, LogoutListener {

  private static final Pattern sAbbreviationPattern = Pattern.compile("(股份)*(责任)*(有限)*(股份)*(责任)*第*(一|二|三|四|五|六|七|八|九|十|零)*分*(公司|厂)|集团|\\(.+\\)|（.+）");

  @InjectView (R.id.et_circle_name)
  public EditText mEtCircleName;

  @InjectView (R.id.et_circle_abbreviation)
  public EditText mEtCircleAbbreviation;

  @InjectView (R.id.tv_location)
  public TextView mTvLocation;

  @InjectView (R.id.et_captcha)
  public EditText mEtCaptcha;

  @InjectView (R.id.iv_captcha)
  public ImageView mIvCaptcha;

  @InjectView (R.id.rb_reget_captcha)
  public RoundedButton mRbRegetCaptcha;

  @InjectView (R.id.rb_create)
  public RoundedButton mRbCreate;

  private boolean mRequesting;
  private Location.Result mResult;

  public static void start(Context context, String circle) {
    Intent intent = new Intent(context, CircleCreateActivity.class);
    intent.putExtra("circle", circle);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnTextChanged (R.id.et_circle_name)
  public void onEtCircleNameTextChanged(CharSequence t) {
    final int length = U.getConfigInt("circle.length");
    if (t.length() > length) {
      showToast(getString(R.string.circle_name_length_long));
      mEtCircleName.setText(t.subSequence(0, length));
      mEtCircleName.setSelection(length);
    }
  }

  @OnClick (R.id.iv_captcha)
  public void onIvCaptchaClicked() {
    requestCaptcha();
  }

  @OnTextChanged (R.id.et_circle_abbreviation)
  public void onEtCircleAbbreviationTextChanged(CharSequence t) {
    final int length = U.getConfigInt("circle.short.length");
    if (t.length() > length) {
      showToast(getString(R.string.circle_short_name_length_long));
      mEtCircleAbbreviation.setText(t.subSequence(0, length));
      mEtCircleAbbreviation.setSelection(length);
    }
  }

  @OnClick(R.id.tv_location)
  public void onTvLocationClicked() {
    M.getLocation().requestLocation();
    mTvLocation.setText(R.string.locating);
  }

  @OnFocusChange(R.id.et_circle_abbreviation)
  public void onEtCircleAbbreviationFocusChange(boolean focused) {
    if (focused) {
      if (mEtCircleAbbreviation.getText().length() == 0) {
        String text = mEtCircleName.getText().toString();
        text = sAbbreviationPattern.matcher(text).replaceAll("");
        mEtCircleAbbreviation.setText(text);
      }
    }
  }

  @OnClick (R.id.rb_reget_captcha)
  public void onRbRegetCaptchaClicked() {
    requestCaptcha();
  }

  @OnClick (R.id.rb_create)
  public void onRbCreateClicked() {
    requestCreateFactory();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String circle = getIntent().getStringExtra("circle");
    mEtCircleName.setText(circle);

    requestCaptcha();
  }

  @Override
  protected void onResume() {
    super.onResume();

    M.getLocation().onResume(this);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        M.getLocation().requestLocation();
        mTvLocation.setText(getString(R.string.locating));
      }
    }, 1000);
  }

  @Override
  protected void onPause() {
    super.onPause();

    M.getLocation().onPause(this);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onResult(Location.Result result) {
    if (mResult == null) {
      if (result != null && !TextUtils.isEmpty(result.address)) {
        mResult = result;
        mTvLocation.setText(mResult.address);
      } else {
        mTvLocation.setText(getString(R.string.locating_failed));
      }
    }
  }

  /**
   * When LogoutEvent fired, finish myself
   *
   * @param event the logout event
   */
  @Subscribe
  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestCaptcha() {
    if (mRequesting) return;
    mRequesting = true;
    U.getRESTRequester().post(C.API_VALICODE_CREATE_FACTORY, null,
        U.getRESTRequester().addAuthParams(null), null,
        new FileAsyncHttpResponseHandler(IOUtils.createTmpFile("valicode_" + System.currentTimeMillis())) {
          @Override
          public void onSuccess(File file) {
            mIvCaptcha.setImageURI(Uri.fromFile(file));
            mRequesting = false;
            if (file != null) file.delete();
          }

          @Override
          public void onFailure(Throwable e, File response) {
            mRequesting = false;
            if (response != null) response.delete();
          }
        });
  }

  private void requestCreateFactory() {
    CreateCircleRequest request = new CreateCircleRequest(mEtCircleName.getText().toString(),
        CreateCircleRequest.TYPE_FACTORY,
        mEtCircleAbbreviation.getText().toString(),
        mEtCaptcha.getText().toString());

    request(request, new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        hideProgressBar();
        mEtCaptcha.setText("");
        mRbCreate.setEnabled(true);
        if (response != null && response.code == 0) {
          showToast(getString(R.string.success_created), false);
          finish();
        } else {
          requestCaptcha();
        }
      }
    }, Response.class);

    mRbCreate.setEnabled(false);
    showProgressBar(true);
  }
}