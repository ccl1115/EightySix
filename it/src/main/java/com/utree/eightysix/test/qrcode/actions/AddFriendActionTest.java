package com.utree.eightysix.test.qrcode.actions;

import android.net.Uri;
import android.test.suitebuilder.annotation.SmallTest;
import com.utree.eightysix.qrcode.actions.AddFriendAction;
import com.utree.eightysix.test.BaseAndroidTestCase;
import com.utree.eightysix.test.BaseInstrumentationTestCase;
import junit.framework.Assert;

/**
 * @author simon
 */
public class AddFriendActionTest extends BaseInstrumentationTestCase {

  private AddFriendAction mAddFriendAction;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    mAddFriendAction = new AddFriendAction();
  }

  @SmallTest
  public void testAccept() throws Throwable {
    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        String content = "eightysix://friend/add/123";
        Uri uri = Uri.parse(content);
        Assert.assertEquals(true, mAddFriendAction.accept(uri));
      }
    });
  }
}
