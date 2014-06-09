package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.request.FindPwd1Request;
import com.utree.eightysix.request.FindPwd2Request;
import com.utree.eightysix.request.FindPwd3Request;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import com.utree.eightysix.widget.RoundedButton;
import java.util.Date;

/**
 */
@Layout (R.layout.activity_forget_pwd)
@TopTitle (R.string.find_password)
public class ForgetPwdActivity extends BaseActivity {

    private static final int MSG_COUNTDOWN = 0x1;

    @ViewId (R.id.page_1)
    public LinearLayout mPage1;

    @ViewId (R.id.et_phone_number)
    public EditText mEtPhoneNumber;

    @ViewId (R.id.et_captcha)
    public EditText mEtCaptcha;

    @ViewId (R.id.page_2)
    public LinearLayout mPage2;

    @ViewId (R.id.btn_get_captcha)
    @OnClick
    public RoundedButton mBtnGetCaptcha;

    @ViewId (R.id.et_pwd)
    public EditText mEtNewPwd;

    @ViewId (R.id.btn_done)
    @OnClick
    public RoundedButton mBtnDone;

    @ViewId (R.id.btn_ok_1)
    @OnClick
    public RoundedButton mBtnOk1;

    @ViewId (R.id.receiving_captcha)
    public TextView mTvReceivingCaptcha;

    private long mTargetTime;


    private boolean mPhoneNumberCorrect;

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.btn_ok_1:
                requestFindPwd2();
                break;
            case R.id.btn_get_captcha:
                requestFindPwd1();
                break;
            case R.id.btn_done:
                requestFindPwd3();
                break;
            default:
                break;
        }
    }

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

                mBtnGetCaptcha.setEnabled(mPhoneNumberCorrect = InputValidator.phoneNumber(s));
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
                if (TextUtils.isEmpty(s) && mPhoneNumberCorrect) {
                    mBtnDone.setEnabled(false);
                } else {
                    mBtnDone.setEnabled(true);
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

    @Override
    protected void handleMessage(Message message) {
        if (message.what == MSG_COUNTDOWN) {
            final long now = new Date().getTime();
            if (now < mTargetTime) {
                final int t = (int) ((mTargetTime - now) / 1000);
                mBtnGetCaptcha.setText(String.format(getString(R.string.reget_captcha), t));
                getHandler().sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
            } else {
                mBtnGetCaptcha.setText(R.string.get_captcha);
                mBtnGetCaptcha.setEnabled(true);
            }
        }
    }

    private void showPage1() {
        mPage1.setVisibility(View.VISIBLE);
        mPage2.setVisibility(View.GONE);
    }

    private void showPage2() {
        mPage1.setVisibility(View.GONE);
        mPage2.setVisibility(View.VISIBLE);
    }

    private void requestFindPwd1() {
        request(new FindPwd1Request(mEtPhoneNumber.getText().toString()),
                new OnResponse<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null) {
                            if (response.code == 0) {
                                showToast(R.string.request_success);
                                startCountdown();
                                mTvReceivingCaptcha.setText(String.format(getString(R.string.receiving_captcha),
                                        mEtPhoneNumber.getText().toString()));
                            } else {
                                mBtnGetCaptcha.setEnabled(true);
                            }
                        } else {
                            mBtnGetCaptcha.setEnabled(true);
                        }
                    }
                }, Response.class);
        mBtnGetCaptcha.setEnabled(false);
    }

    private void requestFindPwd2() {
        request(new FindPwd2Request(mEtPhoneNumber.getText().toString(), mEtCaptcha.getText().toString()),
                new OnResponse<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null) {
                            if (response.code == 0) {
                                showPage2();
                            } else {
                                mBtnOk1.setEnabled(true);
                            }
                        } else {
                            mBtnOk1.setEnabled(true);
                        }
                    }
                }, Response.class);
        mBtnOk1.setEnabled(false);
    }

    private void requestFindPwd3() {
        request(new FindPwd3Request(mEtPhoneNumber.getText().toString(), mEtNewPwd.getText().toString()),
                new OnResponse<Response>() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null) {
                            if (response.code == 0) {
                                showToast(getString(R.string.new_pwd_set), false);
                                finish();
                                startActivity(new Intent(ForgetPwdActivity.this, LoginActivity.class));
                            } else {
                                mBtnDone.setEnabled(true);
                            }
                        } else {
                            mBtnDone.setEnabled(true);
                        }
                    }
                }, Response.class);
        mBtnDone.setEnabled(false);
    }

    private void startCountdown() {
        mTargetTime = new Date().getTime() + U.getConfigInt("activity.find_pwd.captcha.countdown");

        getHandler().sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
    }
}