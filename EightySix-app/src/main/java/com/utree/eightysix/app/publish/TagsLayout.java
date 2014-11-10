package com.utree.eightysix.app.publish;

import android.content.Context;
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
public class TagsLayout extends FloatingLayout {


  private int mCount;

  private List<Tag> mSelectedTags = new ArrayList<Tag>();

  private OnSelectedTagsChangedListener mOnSelectedTagsChangedListener;

  public TagsLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setTag(List<Tag> tags) {

    removeAllViews();

    final int padding = U.dp2px(16);
    setPadding(padding, padding, padding, padding);

    List<TextView> tagViews = buildSpannable(tags);

    for (TextView v : tagViews) {
      addView(v);
    }
  }

  public boolean hasTags() {
    return getChildCount() > 0;
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

      if (mSelectedTags.contains(g)) {
        view.setSelected(true);
      }

      view.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean selected = v.isSelected();
          U.getAnalyser().trackEvent(getContext(), "publish_tag_select",
              ((Tag) v.getTag()).content, selected ? "selected" : "unselected");
          if (!selected && mCount == 3) {
            U.showToast("最多只能选择三个标签哦");
          } else {
            if (selected) {
              mSelectedTags.remove(v.getTag());
              mCount = mCount - 1;
            } else {
              mSelectedTags.add((Tag) v.getTag());
              mCount = mCount + 1;
            }
            v.setSelected(!selected);

            if (mOnSelectedTagsChangedListener != null) {
              mOnSelectedTagsChangedListener.onSelectedTagsChanged(mSelectedTags);
            }
          }
        }
      });

      final int padding = U.dp2px(8);
      view.setPadding(padding, padding, padding, padding);

      view.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4),
          getContext().getResources().getColorStateList(R.color.apptheme_primary_white_btn)));
      view.setTextColor(getResources().getColorStateList(R.color.apptheme_primary_text_dark));
      views.add(view);
    }
    return views;
  }

  public void setSelectedTags(List<Tag> tags) {
    mSelectedTags = tags;
    mCount = mSelectedTags.size();
  }

  public List<Tag> getSelectedTags() {
    return mSelectedTags;
  }

  public void setOnSelectedTagsChangedListener(OnSelectedTagsChangedListener listener) {
    mOnSelectedTagsChangedListener = listener;
  }

  public interface OnSelectedTagsChangedListener {
    void onSelectedTagsChanged(List<Tag> tags);
  }
}
