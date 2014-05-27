package com.utree.eightysix.test.app.account;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import com.robotium.solo.Solo;
import com.utree.eightysix.app.account.LoginActivity;

/**
 */
public class TestLoginActivity extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo mSolo;

    public TestLoginActivity() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mSolo = new Solo(getInstrumentation(), getActivity());
    }

    public void testEditTextInput() throws Exception {
        EditText etPhoneNumber = mSolo.getEditText(0);
        mSolo.enterText(etPhoneNumber, "9999999999999");
        assertEquals(etPhoneNumber.getText().length(), 11);

        mSolo.clearEditText(etPhoneNumber);
        mSolo.enterText(etPhoneNumber, "not a number");
    }
}
