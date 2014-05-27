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
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.request.LoginRequest;
import com.utree.eightysix.utils.JsonHttpResponseHandler;
import com.utree.eightysix.utils.ViewBinding;
import de.akquinet.android.androlog.Log;
import org.apache.http.Header;

/**
 */
public class LoginActivity extends BaseActivity {

    @ViewBinding.ViewId(R.id.btn_login)
    @ViewBinding.OnClick
    public Button mBtnLogin;

    @ViewBinding.ViewId(R.id.et_pwd)
    public EditText mEtPwd;

    @ViewBinding.ViewId(R.id.et_phone_number)
    public EditText mEtPhoneNumber;

    private boolean mCorrectPhoneNumber;

    private boolean mCorrectPwd;

    private int mPhoneNumberLength = U.getConfigInt("account.phone.length");

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

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
                } else if (s.length() < 11) {
                    mCorrectPhoneNumber = false;
                    mBtnLogin.setEnabled(false);
                } else {
                    mCorrectPhoneNumber = true;
                    if (mCorrectPwd) {
                        mBtnLogin.setEnabled(true);
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
                if (s.length() >= U.getConfigInt("account.pwd.length.min")
                        && s.length() <= U.getConfigInt("account.pwd.length.max")) {
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

    private void requestLogin() {
        request(new LoginRequest("18478737847", "test-password"), new JsonHttpResponseHandler<Object>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                mBtnLogin.setEnabled(true);
            }

            @Override
            public void onFinish() {
            }

            @Override
            public Object parseResponse(String responseBody) throws Throwable {
                return null;
            }
        });
        mBtnLogin.setEnabled(false);
    }
}