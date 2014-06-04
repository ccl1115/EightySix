package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.aliyun.android.oss.model.User;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.request.RegisterRequest;
import com.utree.eightysix.response.OnResponse;
import com.utree.eightysix.response.Response;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import com.utree.eightysix.widget.RoundedButton;

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

    @ViewId(R.id.tv_login)
    @OnClick
    public TextView mTvLogin;

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
            case R.id.tv_login:
                startActivity(new Intent(this, LoginActivity.class));
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
    }

    private void requestRegister() {
        request(new RegisterRequest(mEtPhoneNumber.getText().toString(), mEtPwd.getText().toString()),
                new OnResponse<Response<User>>() {
                    @Override
                    public void onResponse(Response<User> response) {
                        if (response == null) {
                            mBtnRegister.setEnabled(true);
                        } else {
                            finish();
                        }
                    }
                });

        mBtnRegister.setEnabled(false);
    }
}