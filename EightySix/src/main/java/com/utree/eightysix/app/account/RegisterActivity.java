package com.utree.eightysix.app.account;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.request.RegisterRequest;
import com.utree.eightysix.response.data.User;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.response.UserResponse;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {

    @ViewId(R.id.et_phone_number)
    public EditText mEtPhoneNumber;

    @ViewId(R.id.et_pwd)
    public EditText mEtPwd;

    @ViewId(R.id.btn_register)
    @OnClick
    public RoundedButton mBtnRegister;

    private boolean mCorrectPhoneNumber;
    private boolean mCorrectPwd;

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.btn_register:
                requestRegister();
                break;
            default:
                break;
        }
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


                if (InputValidator.phoneNumber(s)) {
                    mCorrectPhoneNumber = true;
                    if (mCorrectPwd) {
                        mBtnRegister.setEnabled(true);
                    }
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
                        mBtnRegister.setEnabled(true);
                    }
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
                    }
                }, UserResponse.class);

        mBtnRegister.setEnabled(false);
    }

    @Subscribe public void onLoginEvent(Account.LoginEvent event) {
        showToast(R.string.register_success, false);
        finish();
    }
}