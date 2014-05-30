package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;

/**
 */
@Layout(R.layout.activity_forget_pwd)
@TopTitle(R.string.find_password)
public class ForgetPwdActivity extends BaseActivity {

    @ViewId(R.id.page_1)
    public LinearLayout mPage1;

    @ViewId(R.id.et_phone_number)
    public EditText mEtPhoneNumber;

    @ViewId(R.id.btn_ok_1)
    @OnClick
    public Button mBtnOk1;

    @ViewId(R.id.page_2)
    public LinearLayout mPage2;

    @ViewId(R.id.tv_notice)
    public TextView mTVNotice;

    @ViewId(R.id.et_captcha)
    public EditText mEtCaptcha;

    @ViewId(R.id.btn_ok_2)
    @OnClick
    public Button mBtnOk2;

    @ViewId(R.id.page_3)
    public LinearLayout mPage3;

    @ViewId(R.id.et_new_pwd)
    public EditText mEtNewPwd;

    @ViewId(R.id.btn_done)
    @OnClick
    public Button mBtnDone;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int phoneLength = U.getConfigInt("account.phone.length");
                if (s.length() > phoneLength) {
                    mEtPhoneNumber.setText(s.subSequence(0, phoneLength));
                    mEtPhoneNumber.setSelection(phoneLength);
                }

                if (InputValidator.phoneNumber(s)) {
                    mBtnOk1.setEnabled(true);
                } else {
                    mBtnOk1.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtCaptcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mBtnOk2.setEnabled(false);
                } else {
                    mBtnOk2.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (InputValidator.pwd(s)) {
                    mBtnDone.setEnabled(true);
                } else {
                    mBtnDone.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showPage1() {
        mPage1.setVisibility(View.VISIBLE);
        mPage2.setVisibility(View.GONE);
        mPage3.setVisibility(View.GONE);
    }

    private void showPage2() {
        mPage1.setVisibility(View.GONE);
        mPage2.setVisibility(View.VISIBLE);
        mPage3.setVisibility(View.GONE);
    }

    private void showPage3() {
        mPage1.setVisibility(View.GONE);
        mPage2.setVisibility(View.GONE);
        mPage3.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.btn_ok_1:
                break;
            case R.id.btn_ok_2:
                break;
            case R.id.btn_done:
                break;
            default:
                break;
        }
    }
}