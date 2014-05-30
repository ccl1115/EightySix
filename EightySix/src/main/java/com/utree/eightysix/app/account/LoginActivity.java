package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.request.LoginRequest;
import com.utree.eightysix.response.OnResponse;
import com.utree.eightysix.response.Response;
import com.utree.eightysix.response.User;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;

/**
 */
@Layout(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewId(R.id.btn_login)
    @OnClick
    public Button mBtnLogin;

    @ViewId(R.id.et_pwd)
    public EditText mEtPwd;

    @ViewId(R.id.et_phone_number)
    public EditText mEtPhoneNumber;

    private boolean mCorrectPhoneNumber;

    private boolean mCorrectPwd;

    private int mPhoneNumberLength = U.getConfigInt("account.phone.length");

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.btn_login:
                requestLogin();
                break;
            default:
                break;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTopTitle(getString(R.string.login) + getString(R.string.app_name));

        mEtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > mPhoneNumberLength) {
                    mEtPhoneNumber.setText(s.subSequence(0, mPhoneNumberLength));
                    mEtPhoneNumber.setSelection(11);
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

    }

    private void requestLogin() {
        request(new LoginRequest("18478737847", "test-password"), new OnResponse<Response<User>>() {
            @Override
            public void onResponse(Response<User> response) {
                if (response.code == 0) {
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, response.message, Toast.LENGTH_LONG).show();
                }
            }
        });
        mBtnLogin.setEnabled(false);
    }
}