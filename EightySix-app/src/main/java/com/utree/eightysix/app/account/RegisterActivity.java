package com.utree.eightysix.app.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.intro.IntroActivity;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.User;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.RegisterRequest;
import com.utree.eightysix.response.UserResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout (R.layout.activity_register)
public class RegisterActivity extends BaseActivity {

  @InjectView (R.id.et_phone_number)
  public EditText mEtPhoneNumber;

  @InjectView (R.id.et_pwd)
  public EditText mEtPwd;

  @InjectView (R.id.btn_register)
  public RoundedButton mBtnRegister;

  @InjectView (R.id.btn_import_contact)
  public RoundedButton mRbImportContact;

  private boolean mCorrectPhoneNumber;
  private boolean mCorrectPwd;

  public static void start(Context context, String number) {
    Intent intent = new Intent(context, RegisterActivity.class);
    intent.putExtra("phoneNumber", number);
    context.startActivity(intent);
  }

  @OnClick (R.id.btn_register)
  public void onBtnRegisterClicked() {
    requestRegister();
  }

  @OnClick (R.id.btn_import_contact)
  public void onBtnImportContactClicked() {
    startActivity(new Intent(this, ImportContactActivity.class));
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    setTopTitle(getString(R.string.register) + getString(R.string.app_name));

    mRbImportContact.setVisibility(U.useFixture() ? View.VISIBLE : View.GONE);

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

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        if (position == 0) {
          return getString(R.string.login);
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
          LoginActivity.start(RegisterActivity.this, mEtPhoneNumber.getText().toString());
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

    request(new RegisterRequest(mEtPhoneNumber.getText().toString(), mEtPwd.getText().toString()),
        new OnResponse<UserResponse>() {
          @Override
          public void onResponse(UserResponse response) {
            if (response != null) {
              if (response.code == 0) {
                User user = response.object;
                if (user != null) {
                  Account.inst().login(user.userId, user.token);
                  showToast(R.string.register_success, false);
                  //startActivity(new Intent(RegisterActivity.this, ImportContactActivity.class));
                  //finish();
                  setLoadingText("寻找工友圈中");
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
        }, UserResponse.class);

    showProgressBar();
    mBtnRegister.setEnabled(false);
  }

  @Subscribe
  public void onContactsSyncEvent(ContactsSyncEvent event) {
    BaseCirclesActivity.startSelect(this);
    finish();
  }

  @Subscribe
  public void onLoginEvent(Account.LoginEvent event) {
  }
}