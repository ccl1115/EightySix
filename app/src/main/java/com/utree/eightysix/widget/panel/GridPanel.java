package com.utree.eightysix.widget.panel;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author simon
 */
public class GridPanel extends ViewPager {

  private Panel mPanel;

  public GridPanel(Context context) {
    this(context, null);
  }

  public GridPanel(Context context, AttributeSet attrs) {
    super(context, attrs);

    try {
      XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
      parser.setInput(getResources().openRawResource(R.raw.publish_panel), "UTF-8");
      mPanel = new Panel(parser);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    }

    setAdapter(new PanelAdapter());
  }

  public List<Item> getItemsByPage(int page) {
    return mPanel.getPages().get(page).getItems();
  }

  private class PanelAdapter extends PagerAdapter {

    @Override
    public int getCount() {
      return mPanel.getPages().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      PageView pageView = new PageView(getContext(), mPanel.getPages().get(position));
      pageView.setLayoutParams(new LayoutParams());
      container.addView(pageView);
      return pageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec,
        mPanel.getItemHeight() * mPanel.getPageRow() + (mPanel.getPageRow() + 1) * mPanel.getSpaceVertical() + MeasureSpec.AT_MOST);
  }
}
