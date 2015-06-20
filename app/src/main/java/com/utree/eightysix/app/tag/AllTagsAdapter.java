/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.tag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedsSearchActivity;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.RoundedButton;

import java.util.*;

/**
 */
public class AllTagsAdapter extends BaseAdapter {

  private static final int TYPE_HEAD = 0;
  private static final int TYPE_TAGS = 1;

  private Map<String, List<Tag>> mTags;

  private List<String> mKeys;
  private Random mRandom = new Random();

  public AllTagsAdapter(List<Tag> tags) {
    mTags = new HashMap<String, List<Tag>>();
    mKeys = new ArrayList<String>();
    for (Tag t : tags) {
      List<Tag> sub = mTags.get(t.typeName);
      if (sub == null) {
        sub = new ArrayList<Tag>();
        mKeys.add(t.typeName);
        mTags.put(t.typeName, sub);
      }

      sub.add(t);
    }
  }


  @Override
  public int getCount() {
    return mTags.size() * 2;
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    return position % 2 == 0 ? TYPE_HEAD : TYPE_TAGS;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_HEAD:
        return getHeadView(position, convertView, parent);
      case TYPE_TAGS:
        return getTagsView(position, convertView, parent);
    }
    return null;
  }

  private View getTagsView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new TableLayout(parent.getContext());
    }

    final List<Tag> tags = mTags.get(mKeys.get((position - 1) >> 1));

    if (tags.size() % 3 == 1) {
      tags.add(null);
      tags.add(null);
    } else if (tags.size() % 3 == 2) {
      tags.add(null);
    }

    TableLayout tl = (TableLayout) convertView;

    final int length = U.dp2px(70);
    final int margin = U.dp2px(18);
    for (int i = 0, size = tags.size(); i < size; i += 3) {
      TableRow row = new TableRow(parent.getContext());
      row.setPadding(0, margin, 0, 0);

      if (tags.get(i) == null) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, length, 1);
        lp.column = 0;
        lp.rightMargin = margin;
        View view = new View(parent.getContext());
        view.setLayoutParams(lp);
        row.addView(view);
      } else {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, length, 1);
        lp.column = 0;
        lp.rightMargin = margin;
        RoundedButton roundedButton = new RoundedButton(parent.getContext());
        roundedButton.setText(tags.get(i).content);
        roundedButton.setTextSize(14);
        roundedButton.setRadius(U.dp2px(8));
        roundedButton.setLayoutParams(lp);
        roundedButton.setBackgroundColor(ColorUtil.getRandomColor());
        final int finalI = i;
        roundedButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            FeedsSearchActivity.start(v.getContext(), tags.get(finalI).content);
          }
        });
        row.addView(roundedButton);
      }

      if (tags.get(i + 1) == null) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, length, 1);
        lp.column = 1;
        lp.leftMargin = margin;
        lp.rightMargin = margin;
        View view = new View(parent.getContext());
        view.setLayoutParams(lp);
        row.addView(view);
      } else {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, length, 1);
        lp.column = 1;
        lp.leftMargin = margin;
        lp.rightMargin = margin;
        RoundedButton roundedButton = new RoundedButton(parent.getContext());
        roundedButton.setText(tags.get(i + 1).content);
        roundedButton.setTextSize(14);
        roundedButton.setRadius(U.dp2px(8));
        roundedButton.setBackgroundColor(ColorUtil.getRandomColor());
        roundedButton.setLayoutParams(lp);
        final int finalI = i;
        roundedButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            FeedsSearchActivity.start(v.getContext(), tags.get(finalI + 1).content);
          }
        });
        row.addView(roundedButton);
      }

      if (tags.get(i + 2) == null) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, length, 1);
        lp.column = 2;
        lp.leftMargin = margin;
        View view = new View(parent.getContext());
        view.setLayoutParams(lp);
        row.addView(view);
      } else {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, length, 1);
        lp.column = 2;
        lp.leftMargin = margin;
        RoundedButton roundedButton = new RoundedButton(parent.getContext());
        roundedButton.setText(tags.get(i + 2).content);
        roundedButton.setTextSize(14);
        roundedButton.setRadius(U.dp2px(8));
        roundedButton.setBackgroundColor(ColorUtil.getRandomColor());
        roundedButton.setLayoutParams(lp);
        final int finalI = i;
        roundedButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            FeedsSearchActivity.start(v.getContext(), tags.get(finalI + 2).content);
          }
        });
        row.addView(roundedButton);
      }

      tl.addView(row);
    }

    return convertView;
  }

  private View getHeadView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_head, parent, false);
    }

    ((TextView) convertView.findViewById(R.id.tv_head)).setText(mKeys.get(position >> 1));

    return convertView;
  }
}
