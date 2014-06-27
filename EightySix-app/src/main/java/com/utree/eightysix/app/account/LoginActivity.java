package com.utree.eightysix.app.account;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.request.LoginRequest;
import com.utree.eightysix.response.UserResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TopBar;

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

  @InjectView (R.id.tv_forget_pwd)
  public TextView mTvForgetPwd;

  @InjectView(R.id.btn_fixture)
  public RoundedButton mBtnFixture;

  private boolean mCorrectPhoneNumber;

  private boolean mCorrectPwd;

  private int mPhoneNumberLength = U.getConfigInt("account.phone.length");

  @OnClick (R.id.btn_login)
  public void onBtnLoginClicked() {
    requestLogin();
  }

  @OnClick (R.id.tv_forget_pwd)
  public void onTvForgetPwd() {
    startActivity(new Intent(this, ForgetPwdActivity.class));
  }

  @OnClick(R.id.btn_fixture)
  public void onBtnFixtureClicked() {
    FeedActivity.start(this, null);
    finish();
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTopTitle(getString(R.string.login) + getString(R.string.app_name));

    if(U.useFixture()) {
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
        s = InputValidator.trimPwd(s);

        final int selection = mEtPwd.getSelectionStart();
        mEtPwd.setText(s);
        mEtPwd.setSelection(Math.min(selection, s.length()));

        if (InputValidator.pwd(s)) {
          mCorrectPwd = true;
          if (mCorrectPhoneNumber) {
            mBtnLogin.setEnabled(true);
          }
        } else {
          mCorrectPwd = false;
          mBtnLogin.setEnabled(false);
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

  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }

  private void requestLogin() {
    request(new LoginRequest(mEtPhoneNumber.getText().toString(), mEtPwd.getText().toString()),
        new OnResponse<UserResponse>() {
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
              }
            }
            mBtnLogin.setEnabled(true);
            hideProgressBar();
          }
        }, UserResponse.class);
    mBtnLogin.setEnabled(false);
    showProgressBar();
  }

  private class ActionAdapter implements TopBar.ActionAdapter {
    @Override
    public String getTitle(int position) {
      if (position == 0) {
        return getString(R.string.register);
      }
      return null;
    }

    @Override
    public Drawable getIcon(int position) {
      return null;
    }

    @Override
    public Drawable getBackgroundDrawable(int position) {
      if (position == 0) return U.gd(R.drawable.apptheme_primary_btn_dark);

      return null;
    }

    @Override
    public void onClick(View view, int position) {
      if (position == 0) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
      }
    }

    @Override
    public int getCount() {
      return 1;
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams(int position) {
      return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
  }
}