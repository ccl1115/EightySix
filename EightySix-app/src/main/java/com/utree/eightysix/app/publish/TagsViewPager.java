package com.utree.eightysix.app.publish;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.widget.FloatLayout;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TagsViewPager extends ViewPager {

  private List<Tag> mTags;

  public TagsViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setTag(List<Tag> tags) {
    mTags = tags;

    setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return Math.round(mTags.size() / 10f);
      }

      @Override
      public Object instantiateItem(ViewGroup container, int position) {
        FloatLayout child = new FloatLayout(container.getContext());

        List<TextView> tagViews = buildSpannable(mTags.subList(position * 10,
            Math.min(mTags.size(), (position + 1) * 10 - 1)));

        for (TextView v : tagViews) {
          child.addView(v);
        }
        container.addView(child);
        return child;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
      }

      @Override
      public boolean isViewFromObject(View view, Object object) {
        return object.equals(view);
      }
    });
  }

  public List<TextView> buildSpannable(List<Tag> tags) {
    List<TextView> views = new ArrayList<TextView>();
    for (Tag g : tags) {
      TextView view = new TextView(getContext());
      view.setText(g.content);
      view.setTag(g.id);
      view.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4),
          getContext().getResources().getColorStateList(R.color.apptheme_primary_white_btn)));
      views.add(view);
    }
    return views;
  }

}
