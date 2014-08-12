package com.utree.eightysix.app.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.LoginRequest;
import com.utree.eightysix.response.UserResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TopBar;
import java.io.File;

/**
 */
@Layout (R.layout.activity_login)
public class LoginActivity extends BaseActivity {

  @InjectView (R.id.btn_login)
  public RoundedButton mBtnLogin;

  @InjectView (R.id.et_pwd)
  public EditText mEtPwd;

  @InjectView (R.id.et_phone_number)
  public EditText mEtPhoneNumber;

  @InjectView (R.id.btn_fixture)
  public RoundedButton mBtnFixture;

  @InjectView (R.id.ll_captcha)
  public LinearLayout mLlCaptcha;

  @InjectView (R.id.iv_captcha)
  public ImageView mIvCaptcha;

  @InjectView (R.id.et_captcha)
  public EditText mEtCaptcha;

  private boolean mCorrectPhoneNumber;

  private boolean mCorrectPwd;

  private int mPhoneNumberLength = U.getConfigInt("account.phone.length");

  private boolean mRequesting = false;

  public static void start(Context context, String phoneNumber) {
    Intent intent = new Intent(context, LoginActivity.class);
    intent.putExtra("phone", phoneNumber);
    context.startActivity(intent);
  }

  @OnClick (R.id.btn_login)
  public void onBtnLoginClicked() {
    requestLogin();
  }

  @OnClick (R.id.btn_fixture)
  public void onBtnFixtureClicked() {
    FeedActivity.start(this, null);
    finish();
  }

  @OnClick (R.id.iv_captcha)
  public void onIvCaptchaClicked() {
    requestCaptcha();
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTopTitle(getString(R.string.login) + getString(R.string.app_name));


    if (U.useFixture()) {
      mBtnFixture.setVisibility(View.VISIBLE);
    } else {
      mBtnFixture.setVisibility(View.GONE);
    }

    mEtPhoneNumber.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > mPhoneNumberLength) {
          s = s.subSequence(0, mPhoneNumberLength);
          final int selection = mEtPhoneNumber.getSelectionStart();
          mEtPhoneNumber.setText(s);
          mEtPhoneNumber.setSelection(Math.min(selection, s.length()));
        }

        if (InputValidator.phoneNumber(s)) {
          mCorrectPhoneNumber = true;
          if (mCorrectPwd) {
            mBtnLogin.setEnabled(true);
          }
        } else {
          mCorrectPhoneNumber = false;
          mBtnLogin.setEnabled(false);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    mEtPwd.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (InputValidator.pwd(s)) {
          mCorrectPwd = true;
          if (mCorrectPhoneNumber) {
            mBtnLogin.setEnabled(true);
          }
        } else {
          mCorrectPwd = false;
          mBtnLogin.setEnabled(false);
          showToast("密码格式错误，仅限字母和数字哦");
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    mEtPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
          if (mCorrectPhoneNumber && mCorrectPwd) {
            requestLogin();
          }
        }
        return false;
      }
    });

    getTopBar().setActionAdapter(new ActionAdapter());

    String phone = getIntent().getStringExtra("phone");
    if (phone != null) {
      mEtPhoneNumber.setText(phone);
      mEtPhoneNumber.setSelection(phone.length());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Env.setFirstRun(false);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
  }

  @Subscribe
  public void onLogin(Account.LoginEvent event) {
    finish();
  }

  private void requestLogin() {
    OnResponse<UserResponse> onResponse = new OnResponse<UserResponse>() {
      @Override
      public void onResponse(UserResponse response) {
        if (response != null) {
          if (response.code == 0) {
            if (response.object != null) {
              Account.inst().login(response.object.userId, response.object.token);
              showToast(R.string.login_success, false);
              finish();
              startActivity(new Intent(LoginActivity.this, FeedActivity.class));
            } else {
              showToast(R.string.server_object_error);
            }
          } else if (response.code == 2450 || response.code == 140371) {
            mLlCaptcha.setVisibility(View.VISIBLE);
            requestCaptcha();
          }
        }
        mBtnLogin.setEnabled(true);
        mEtCaptcha.setText("");
        hideProgressBar();
      }
    };

    if (mEtCaptcha.getText().length() > 0) {
      request(new LoginRequest(mEtPhoneNumber.getText().toString(),
              mEtPwd.getText().toString(),
              mEtCaptcha.getText().toString()),
          onResponse, UserResponse.class);
    } else {
      request(new LoginRequest(mEtPhoneNumber.getText().toString(), mEtPwd.getText().toString()),
          onResponse, UserResponse.class);
    }

    mBtnLogin.setEnabled(false);
    showProgressBar();
    hideSoftKeyboard(mEtPwd);
  }

  private void requestCaptcha() {
    if (mRequesting) return;
    mRequesting = true;
    U.getRESTRequester().post(C.API_VALICODE_FIND_PWD, null,
        new RequestParams("phone", mEtPhoneNumber.getText().toString()), null,
        new FileAsyncHttpResponseHandler(IOUtils.createTmpFile("valicode_" + System.currentTimeMillis())) {
          @Override
          public void onSuccess(File file) {
            mIvCaptcha.setImageURI(Uri.fromFile(file));
            if (file != null) {
              file.delete();
            }
            mRequesting = false;
          }

          @Override
          public void onFailure(Throwable e, File response) {
            showToast("获取验证码失败，请重新尝试");
            if (response != null) {
              response.delete();
            }
            mRequesting = false;
          }
        }
    );
  }

  private class ActionAdapter implements TopBar.ActionAdapter {
    @Override
    public String getTitle(int position) {
      if (position == 0) {
        return getString(R.string.forget_pwd);
      }
      return null;
    }

    @Override
    public Drawable getIcon(int position) {
      return null;
    }

    @Override
    public Drawable getBackgroundDrawable(int position) {
      return new RoundRectDrawable(dp2px(2), getResources().getColorStateList(R.color.apptheme_primary_btn_light));
    }

    @Override
    public void onClick(View view, int position) {
      if (position == 0) {
        Intent intent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
        startActivity(intent);
      }
    }

    @Override
    public int getCount() {
      return 1;
    }

    @Override
    public TopBar.LayoutParams getLayoutParams(int position) {
      return new TopBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
  }
}