package com.utree.eightysix.app.account;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.request.RegisterRequest;
import com.utree.eightysix.response.UserResponse;
import com.utree.eightysix.response.data.User;
import com.utree.eightysix.rest.OnResponse;
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

  private boolean mCorrectPhoneNumber;
  private boolean mCorrectPwd;

  @OnClick (R.id.btn_register)
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
          mEtPhoneNumber.setText(s.subSequence(0, phoneLength));
          mEtPhoneNumber.setSelection(phoneLength);
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
        return null;
      }

      @Override
      public void onClick(View view, int position) {
        if (position == 0) {
          startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
      }

      @Override
      public int getCount() {
        return 1;
      }
    });
  }

  @Subscribe
  public void onLoginEvent(Account.LoginEvent event) {
    showToast(R.string.register_success, false);
    finish();
  }

  private void requestRegister() {
    request(new RegisterRequest(mEtPhoneNumber.getText().toString(), mEtPwd.getText().toString()),
        new OnResponse<UserResponse>() {
          @Override
          public void onResponse(UserResponse response) {
            if (response != null) {
              if (response.code == 0) {
                User user = response.object;
                if (user != null) {
                  Account.inst().login(user.userId, user.token);
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
}