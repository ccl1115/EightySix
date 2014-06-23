package com.utree.eightysix.widget.panel;

import com.utree.eightysix.U;
import static com.utree.eightysix.widget.panel.Panel.INHERITED;
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

  private int mRow = INHERITED;
  private int mColumn = INHERITED;
  private int mItemWidth = INHERITED;
  private int mItemHeight = INHERITED;
  private int mSpaceVertical = INHERITED;
  private int mSpaceHorizontal = INHERITED;

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
              mItemWidth = U.dp2px(Integer.parseInt(value));
            } else if ("itemHeight".equals(name)) {
              mItemHeight = U.dp2px(Integer.parseInt(value));
            } else if ("spaceHorizontal".equals(name)) {
              mSpaceHorizontal = U.dp2px(Integer.parseInt(value));
            } else if ("spaceVertical".equals(name)) {
              mSpaceVertical = U.dp2px(Integer.parseInt(value));
            }
          }
        } else if ("item".equals(parser.getName())) {
          mItems.add(new Item(this, parser));
        }
      }
      parser.next();
    }
  }

  public int getSpaceHorizontal() {
    return mSpaceHorizontal == INHERITED ? mParent.getSpaceHorizontal() : mSpaceHorizontal;
  }

  public int getSpaceVertical() {
    return mSpaceVertical == INHERITED ? mParent.getSpaceVertical() : mSpaceVertical;
  }

  public int getRow() {
    return mRow == INHERITED ? mParent.getPageRow() : mRow;
  }

  public int getColumn() {
    return mColumn == INHERITED ? mParent.getPageColumn() : mColumn;
  }

  public int getItemWidth() {
    return mItemWidth == INHERITED ? mParent.getItemWidth() : mItemWidth;
  }

  public int getItemHeight() {
    return mItemHeight == INHERITED ? mParent.getItemHeight() : mItemHeight;
  }

  public List<Item> getItems() {
    return mItems;
  }

  public Panel getParent() {
    return mParent;
  }

}
