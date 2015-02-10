package com.utree.eightysix.app.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.annotations.SerializedName;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.User;
import com.utree.eightysix.request.RegisterRequest;
import com.utree.eightysix.request.RegisterSmsRequest;
import com.utree.eightysix.response.UserResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;

import java.util.Date;

/**
 */
@Layout(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {

  private static final int MSG_COUNTDOWN = 0;
  @InjectView(R.id.et_phone_number)
  public EditText mEtPhoneNumber;

  @InjectView(R.id.et_pwd)
  public EditText mEtPwd;

  @InjectView(R.id.btn_register)
  public RoundedButton mBtnRegister;

  @InjectView(R.id.et_captcha)
  public EditText mEtCaptcha;

  @InjectView(R.id.btn_get_captcha)
  public RoundedButton mRbGetCaptcha;

  @InjectView(R.id.et_invite)
  public EditText mEtInvite;

  private long mTargetTime;

  private boolean mCorrectPhoneNumber;
  private boolean mCorrectPwd;

  public static void start(Context context, String number) {
    Intent intent = new Intent(context, RegisterActivity.class);
    intent.putExtra("phoneNumber", number);
    context.startActivity(intent);
  }

  @OnClick(R.id.btn_get_captcha)
  public void onRbGetCaptchaClicked() {
    requestCaptcha();
    mRbGetCaptcha.setEnabled(false);
  }

  @OnClick(R.id.iv_info)
  public void onIvInfoClicked() {
    requestInfo();
  }

  @OnClick(R.id.btn_register)
  public void onBtnRegisterClicked() {
    requestRegister();
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    setTopTitle(getString(R.string.register) + getString(R.string.app_name));

    mEtPhoneNumber.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        final int phoneLength = U.getConfigInt("account.phone.length");
        if (s.length() > phoneLength) {
          final int selection = mEtPhoneNumber.getSelectionStart();
          s = s.subSequence(0, phoneLength);
          mEtPhoneNumber.setText(s);
          mEtPhoneNumber.setSelection(Math.min(selection, s.length()));
        }


        mCorrectPhoneNumber = InputValidator.phoneNumber(s);
        if (mCorrectPhoneNumber) {
          if (mCorrectPwd) {
            mBtnRegister.setEnabled(true);
          }
          mRbGetCaptcha.setEnabled(true);
        } else {
          mBtnRegister.setEnabled(false);
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
        mCorrectPwd = InputValidator.pwd(s);
        if (mCorrectPwd) {
          if (mCorrectPhoneNumber) {
            mBtnRegister.setEnabled(true);
          }
        } else {
          mBtnRegister.setEnabled(false);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    String phoneNumber = getIntent().getStringExtra("phoneNumber");

    if (phoneNumber != null) {
      mEtPhoneNumber.setText(phoneNumber);
      mEtPhoneNumber.setSelection(mEtPhoneNumber.getText().length());
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
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestRegister() {

    if (!InputValidator.pwdRegex(mEtPwd.getText().toString())) {
      showToast("密码格式错误，仅限字母和数字哦");
      return;
    }

    OnResponse<UserResponse> onResponse = new OnResponse<UserResponse>() {
      @Override
      public void onResponse(UserResponse response) {
        if (response != null) {
          if (response.code == 0) {
            User user = response.object;
            if (user != null) {
              Account.inst().login(user.userId, user.token);
              showToast(R.string.register_success, false);
              setLoadingText("身份验证中");
              ContactsSyncService.start(RegisterActivity.this, true);
              return;
            } else {
              showToast(R.string.server_object_error);
            }
          }
        }
        mBtnRegister.setEnabled(true);
        hideProgressBar();
      }
    };

    if (TextUtils.isEmpty(mEtInvite.getText().toString())) {
      request(new RegisterRequest(mEtPhoneNumber.getText().toString(), mEtPwd.getText().toString(), mEtCaptcha.getText().toString()),
          onResponse, UserResponse.class);
    } else {
      request(new RegisterRequest(mEtPhoneNumber.getText().toString(),
              mEtPwd.getText().toString(),
              mEtCaptcha.getText().toString(),
              mEtInvite.getText().toString()),
          onResponse, UserResponse.class);
    }

    showProgressBar();
    hideSoftKeyboard(mEtPwd);
    mBtnRegister.setEnabled(false);
  }

  private void requestInfo() {
    showProgressBar(true);
    U.request("invite_code_desc", new OnResponse2<InviteCodeDescResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(InviteCodeDescResponse response) {
        hideProgressBar();

        final ThemedDialog dialog = new ThemedDialog(RegisterActivity.this);
        dialog.setTitle("邀请码的意义");

        TextView textView = new TextView(RegisterActivity.this);

        textView.setText(response.object.desc);
        textView.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
        textView.setGravity(Gravity.CENTER);
        textView.setEms(12);

        dialog.setContent(textView);

        dialog.setPositive("知道了", new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
          }
        });

        dialog.show();
      }
    }, InviteCodeDescResponse.class);
  }

  private void requestCaptcha() {
    request(new RegisterSmsRequest(mEtPhoneNumber.getText().toString()), new OnResponse2<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          showToast("发送验证码成功。");
          startCountdown();
        } else {
          mRbGetCaptcha.setEnabled(true);
        }
      }

      @Override
      public void onResponseError(Throwable e) {
        mRbGetCaptcha.setEnabled(true);
      }
    }, Response.class);
  }

  @Subscribe
  public void onContactsSyncEvent(ContactsSyncEvent event) {
    BaseCirclesActivity.startSelect(this, false);
    finish();
  }

  @Subscribe
  public void onLoginEvent(Account.LoginEvent event) {

  }

  private void startCountdown() {
    mTargetTime = new Date().getTime() + U.getConfigInt("activity.find_pwd.captcha.countdown");

    getHandler().sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
  }

  @Override
  protected void onHandleMessage(Message message) {
    if (message.what == MSG_COUNTDOWN) {
      final long now = new Date().getTime();
      if (now < mTargetTime) {
        final int t = (int) ((mTargetTime - now) / 1000);
        mRbGetCaptcha.setText(String.format(getString(R.string.reget_captcha), t));
        getHandler().sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
      } else {
        mRbGetCaptcha.setText(R.string.get_captcha);
        mRbGetCaptcha.setEnabled(true);
      }
    }
  }

  public static class InviteCodeDescResponse extends Response {

    @SerializedName("object")
    public InviteCodeDesc object;
  }

  public static class InviteCodeDesc {

    @SerializedName("desc")
    public String desc;
  }
}