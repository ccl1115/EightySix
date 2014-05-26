package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import static com.utree.eightysix.utils.ViewBinding.ViewId;

/**
 */
public class ForgetPwdActivity extends BaseActivity {

    @ViewId(R.id.page_1)
    public LinearLayout mPage1;

    @ViewId(R.id.page_2)
    public LinearLayout mPage2;

    @ViewId(R.id.page_3)
    public LinearLayout mPage3;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);

        setTopTitle(getString(R.string.find_password));
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
}