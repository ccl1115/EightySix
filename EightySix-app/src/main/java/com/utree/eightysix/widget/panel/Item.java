package com.utree.eightysix.widget.panel;

import android.util.TypedValue;
import com.utree.eightysix.U;
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

  public TypedValue getValue() {
    return mValue;
  }

  private TypedValue mValue;

  public Item(Page parent, XmlPullParser parser) throws XmlPullParserException, IOException {
    mParent = parent;
    int eventType;
    while((eventType = parser.getEventType()) != XmlPullParser.END_TAG) {
      if (eventType == XmlPullParser.START_TAG) {
        if ("color".equals(parser.getName())) {
          mValue = parseColor(parser);
        } else if ("bg-image".equals(parser.getName())) {
          mValue = parseBgImage(parser);
        }
      }
      parser.next();
    }
  }

  private TypedValue parseBgImage(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.next();
    final String url = String.format("http://%s.%s/%s", U.getConfig("storage.bg.bucket.name"), U.getConfig("storage.host"), parser.getText());
    TypedValue tv = new TypedValue();
    tv.string = url;
    tv.type = TypedValue.TYPE_STRING;
    parser.next();
    parser.next();
    return tv;
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

  @Override
  public String toString() {
    return "Item{" +
        "mParent=" + mParent +
        ", mValue=" + mValue +
        '}';
  }
}
