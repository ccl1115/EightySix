package com.utree.eightysix.test.widget.panel;

import android.test.suitebuilder.annotation.SmallTest;
import com.utree.eightysix.test.BaseAndroidTestCase;
import com.utree.eightysix.test.R;
import com.utree.eightysix.widget.panel.Panel;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author simon
 */
@SmallTest
public class PanelTest extends BaseAndroidTestCase {

  public void testNewPanel() throws Exception {
    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
    parser.setInput(getContext().getResources().openRawResource((R.raw.panel)), "UTF-8");
    Panel panel = new Panel(parser);
    assertNotNull(panel);
    assertEquals(1, panel.getPages().size());
    assertEquals(4, panel.getPageRow());
    assertEquals(5, panel.getPageColumn());
    assertEquals(15, panel.getPages().get(0).getItems().size());
  }
}
