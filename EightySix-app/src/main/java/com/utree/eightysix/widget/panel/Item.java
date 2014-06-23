package com.utree.eightysix.widget.panel;

import android.util.TypedValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author simon
 */
public class Item {

  private Page mParent;

  public Page getParent() {
    return mParent;
  }

  public List<TypedValue> getValues() {
    return mValues;
  }

  private List<TypedValue> mValues = new ArrayList<TypedValue>();

  public Item(Page parent, XmlPullParser parser) throws XmlPullParserException, IOException {
    mParent = parent;
    int eventType;
    while((eventType = parser.getEventType()) != XmlPullParser.END_TAG) {
      if (eventType == XmlPullParser.START_TAG) {
        if ("color".equals(parser.getName())) {
          TypedValue value = parseColor(parser);
          if (value != null) mValues.add(value);
        }
      }
      parser.next();
    }
  }

  private TypedValue parseColor(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.next();
    final String text = parser.getText();
    if (text.startsWith("#")) {
      TypedValue tv = new TypedValue();
      tv.type = TypedValue.TYPE_INT_COLOR_ARGB8;
      tv.data = (int) Long.parseLong(text.substring(1), 16);
      parser.next();
      parser.next();
      return tv;
    }
    parser.next();
    parser.next();
    return null;
  }
}
