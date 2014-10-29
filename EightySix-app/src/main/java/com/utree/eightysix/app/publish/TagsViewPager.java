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
import com.utree.eightysix.widget.FloatingLayout;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TagsViewPager extends ViewPager {



  private List<Tag> mTags;

  private List<TextView> mTextViews;

  private int mCount;

  public TagsViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    mTextViews = new ArrayList<TextView>();
  }

  public void setTag(List<Tag> tags) {
    mTags = tags;
    mTextViews.clear();

    setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return Math.round((mTags.size() + 10) / 10f);
      }

      @Override
      public Object instantiateItem(ViewGroup container, int position) {
        FloatingLayout child = new FloatingLayout(container.getContext());

        child.setLayoutParams(new LayoutParams());

        final int padding = U.dp2px(16);
        child.setPadding(padding, padding, padding, padding);

        List<TextView> tagViews = buildSpannable(
            mTags.subList(position * 10, Math.min(mTags.size(), (position + 1) * 10)));

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
      view.setText("#" + g.content);
      view.setTag(g);
      MarginLayoutParams params = new MarginLayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      final int margin = U.dp2px(4);
      params.setMargins(margin, margin, margin, margin);
      view.setLayoutParams(params);


      view.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean selected = v.isSelected();
          if (!selected && mCount == 3) {
            U.showToast("最多只能选择三个标签哦");
          } else {
            mCount = selected ? mCount - 1 : mCount + 1;
            v.setSelected(!selected);
          }
        }
      });

      final int padding = U.dp2px(8);
      view.setPadding(padding, padding, padding, padding);

      view.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4),
          getContext().getResources().getColorStateList(R.color.apptheme_primary_white_btn)));
      views.add(view);

      mTextViews.add(view);
    }
    return views;
  }

  public List<Tag> getSelectedTags() {
    List<Tag> tags = new ArrayList<Tag>();
    for (TextView tv : mTextViews) {
      if (tv.isSelected()) {
        tags.add((Tag) tv.getTag());
      }
    }

    return tags;
  }
}
