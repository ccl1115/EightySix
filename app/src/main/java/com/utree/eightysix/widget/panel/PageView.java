package com.utree.eightysix.widget.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
@SuppressLint("ViewConstructor")
public class PageView extends ViewGroup {

  private Page mPage;

  private List<ItemView> mChildren = new ArrayList<ItemView>();

  public PageView(Context context, Page page) {
    super(context);
    mPage = page;

    for (Item item : page.getItems()) {
      ItemView child = new ItemView(context, item);
      mChildren.add(child);
      addView(child, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = (widthMeasureSpec & ~(0x3 << 30)) - mPage.getSpaceHorizontal() * (mPage.getColumn() + 1);
    final int heightSize = (heightMeasureSpec & ~(0x3 << 30)) - mPage.getSpaceVertical() * (mPage.getRow() + 1);


    int itemWidth;
    if (widthSize >= mPage.getItemWidth() * mPage.getColumn()) {
      itemWidth = mPage.getItemWidth();
    } else {
      itemWidth = (int) (widthSize / (float) mPage.getColumn());
    }

    int itemHeight;
    if (heightSize >= mPage.getItemHeight() * mPage.getRow()) {
      itemHeight = mPage.getItemHeight();
    } else {
      itemHeight = (int) (heightSize / (float) mPage.getRow());
    }

    for (ItemView child : mChildren) {
      measureChild(child, itemWidth + MeasureSpec.EXACTLY, itemHeight + MeasureSpec.EXACTLY);
    }

    int mw = 0;
    switch (widthMeasureSpec & (0x3 << 30)) {
      case MeasureSpec.AT_MOST:
        mw = itemWidth * mPage.getColumn() + (mPage.getColumn() + 1) * mPage.getSpaceHorizontal();
        break;
      case MeasureSpec.EXACTLY:
        mw = widthMeasureSpec & ~(0x3 << 30);
        break;
    }
    setMeasuredDimension(mw,
        itemHeight * mPage.getRow() + (mPage.getRow() + 1) * mPage.getSpaceVertical());
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int left = 0, top = 0;
    final int cw = (int) ((r - l) / (float) mPage.getColumn());
    final int ch = (int) ((b - t) / (float) mPage.getRow());

    for (int i = 0; i < mChildren.size(); i++) {
      if (i % mPage.getColumn() == 0) {
        left = 0;
        top = (i / mPage.getRow()) * ch;
      }

      ItemView itemView = mChildren.get(i);
      final int iw = itemView.getMeasuredWidth();
      final int ih = itemView.getMeasuredHeight();
      itemView.layout(left + ((cw - iw) >> 1), top + ((ch - ih) >> 1),
          left + ((cw + iw) >> 1), top + ((ch + ih) >> 1));

      left += cw;
    }
  }
}
