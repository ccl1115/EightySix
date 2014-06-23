package com.utree.eightysix.widget.panel;

import com.utree.eightysix.U;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author simon
 */
public class Panel {

  static final int INHERITED = -1;

  List<Page> mPages = new ArrayList<Page>();
  private int mPageRow;
  private int mPageColumn;
  private int mItemWidth;
  private int mItemHeight;
  private int mSpaceHorizontal;
  private int mSpaceVertical;

  public Panel(XmlPullParser parser) throws IOException, XmlPullParserException {
    int eventType;
    while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
      if (eventType == XmlPullParser.START_TAG) {
        if ("panel".equals(parser.getName())) {
          for (int i = 0, size = parser.getAttributeCount(); i < size; i++) {
            final String name = parser.getAttributeName(i);
            final String value = parser.getAttributeValue(i);
            if ("row".equals(name)) {
              mPageRow = Integer.parseInt(value);
            } else if ("column".equals(name)) {
              mPageColumn = Integer.parseInt(value);
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
        } else if ("page".equals(parser.getName())) {
          final Page page = new Page(this, parser);
          mPages.add(page);
        }
      }
      parser.next();
    }
  }

  public int getSpaceHorizontal() {
    return mSpaceHorizontal;
  }

  public int getSpaceVertical() {
    return mSpaceVertical;
  }

  public List<Page> getPages() {
    return mPages;
  }

  public int getPageRow() {
    return mPageRow;
  }

  public int getPageColumn() {
    return mPageColumn;
  }

  public int getItemWidth() {
    return mItemWidth;
  }

  public int getItemHeight() {
    return mItemHeight;
  }
}
