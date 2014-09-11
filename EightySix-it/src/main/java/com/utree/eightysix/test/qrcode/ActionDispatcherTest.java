package com.utree.eightysix.test.qrcode;

import android.test.suitebuilder.annotation.SmallTest;
import com.utree.eightysix.qrcode.ActionDispatcher;
import com.utree.eightysix.qrcode.actions.AddFriendAction;
import com.utree.eightysix.test.BaseAndroidTestCase;
import com.utree.eightysix.test.BaseInstrumentationTestCase;
import junit.framework.Assert;

/**
 * @author simon
 */
public class ActionDispatcherTest extends BaseInstrumentationTestCase {

  private ActionDispatcher mActionDispatcher;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    mActionDispatcher = new ActionDispatcher();
    mActionDispatcher.register(new AddFriendAction());
  }

  @SmallTest
  public void testDispatch() throws Throwable {
    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        boolean suc = mActionDispatcher.dispatch("eightysix://friend/add/123");
        Assert.assertEquals(true, suc);
      }
    });
  }
}
