package com.utree.eightysix.widget.panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author simon
 */
public class Page {

  private Panel mParent;

  private int mRow = Panel.INHERITED;
  private int mColumn = Panel.INHERITED;
  private int mItemWidth = Panel.INHERITED;
  private int mItemHeight = Panel.INHERITED;
  private List<Item> mItems = new ArrayList<Item>();

  public Page(Panel parent, XmlPullParser parser) throws XmlPullParserException, IOException {
    mParent = parent;
    int eventType;
    while ((eventType = parser.getEventType()) != XmlPullParser.END_TAG) {
      if (eventType == XmlPullParser.START_TAG) {
        if ("page".equals(parser.getName())) {
          for (int i = 0, size = parser.getAttributeCount(); i < size; i++) {
            final String name = parser.getAttributeName(i);
            final String value = parser.getAttributeValue(i);
            if ("row".equals(name)) {
              mRow = Integer.parseInt(value);
            } else if ("column".equals(name)) {
              mColumn = Integer.parseInt(value);
            } else if ("itemWidth".equals(name)) {
              mItemWidth = Integer.parseInt(value);
            } else if ("itemHeight".equals(name)) {
              mItemHeight = Integer.parseInt(value);
            }
          }
        } else if ("item".equals(parser.getName())) {
          mItems.add(new Item(this, parser));
        }
      }
      parser.next();
    }
  }

  public int getRow() {
    return mRow == Panel.INHERITED ? mParent.getPageRow() : mRow;
  }

  public int getColumn() {
    return mColumn == Panel.INHERITED ? mParent.getPageColumn() : mColumn;
  }

  public int getItemWidth() {
    return mItemWidth == Panel.INHERITED ? mParent.getItemWidth() : mItemWidth;
  }

  public int getItemHeight() {
    return mItemHeight == Panel.INHERITED ? mParent.getItemHeight() : mItemHeight;
  }

  public List<Item> getItems() {
    return mItems;
  }

  public Panel getParent() {
    return mParent;
  }

}
