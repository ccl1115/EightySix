package com.utree.eightysix.test.demo;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;
import com.utree.eightysix.demo.OSSDemoActivity;

/**
 */
public class TestOSSDemoActivity extends ActivityInstrumentationTestCase2<OSSDemoActivity> {

    private Solo mSolo;

    public TestOSSDemoActivity() {
        super(OSSDemoActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mSolo = new Solo(getInstrumentation(), getActivity());
    }

    public void testCreateBucket() throws Exception {
        //mSolo.enterText(0, "utree-test-test-test");
        //mSolo.clickOnButton("Create bucket");
        //mSolo.waitForCondition(new Condition() {
        //    @Override
        //    public boolean isSatisfied() {
        //        return false;
        //    }
        //}, 2000);
    }

    public void testDeleteBucket() throws Exception {
        //mSolo.enterText(0, "utree-test-test-test");
        //mSolo.clickOnButton("Delete bucket");
        //mSolo.waitForCondition(new Condition() {
        //    @Override
        //    public boolean isSatisfied() {
        //        return false;
        //    }
        //}, 2000);
    }
}
