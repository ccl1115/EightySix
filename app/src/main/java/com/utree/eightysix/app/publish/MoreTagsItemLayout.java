package com.utree.eightysix.app.publish;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedsSearchActivity;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.widget.FloatingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MoreTagsItemLayout extends FloatingLayout {


  public MoreTagsItemLayout(Context context, AttributeSet attrs) {
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

  public List<TextView> buildSpannable(List<Tag> tags) {
    List<TextView> views = new ArrayList<TextView>();
    for (Tag g : tags) {
      TextView view = new TextView(getContext());
      view.setText("#" + g.content);
      view.setTag(g);
      MarginLayoutParams params = new MarginLayoutParams(
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      final int margin = U.dp2px(4);
      params.setMargins(margin * 2, margin, margin * 2, margin);
      view.setLayoutParams(params);


      view.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          FeedsSearchActivity.start(getContext(), ((Tag) v.getTag()).content);
        }
      });

      final int padding = U.dp2px(8);
      view.setPadding(padding * 2, padding, padding * 2, padding);

      view.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4),
          getContext().getResources().getColorStateList(R.color.apptheme_primary_white_btn)));
      views.add(view);
    }
    return views;
  }
}
