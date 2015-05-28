/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.utree.eightysix.U;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TextUtils {

  public static List<CharSequence> page(CharSequence content, int width, int height, int textSize) {
    PageSplitter pageSplitter = new PageSplitter(width, height, 1f, 1f);
    pageSplitter.append(content);
    TextPaint textPaint = new TextPaint();
    textPaint.setTextSize(U.dp2px(textSize));
    pageSplitter.split(textPaint);
    return pageSplitter.getPages();
  }

  private static class PageSplitter {
    private int pageWidth;
    private int pageHeight;
    private final float lineSpacingMultiplier;
    private final float lineSpacingExtra;
    private final List<CharSequence> pages = new ArrayList<CharSequence>();
    private SpannableStringBuilder mSpannableStringBuilder = new SpannableStringBuilder();

    public PageSplitter(int pageWidth, int pageHeight, float lineSpacingMultiplier, float lineSpacingExtra) {
      this.pageWidth = pageWidth;
      this.pageHeight = pageHeight;
      this.lineSpacingMultiplier = lineSpacingMultiplier;
      this.lineSpacingExtra = lineSpacingExtra;
    }

    public void append(CharSequence charSequence) {
      mSpannableStringBuilder.append(charSequence);
    }

    public void split(TextPaint textPaint) {
      StaticLayout staticLayout = new StaticLayout(
          mSpannableStringBuilder,
          textPaint,
          pageWidth,
          Layout.Alignment.ALIGN_NORMAL,
          lineSpacingMultiplier,
          lineSpacingExtra,
          false
      );
      int startLine = 0;
      while(startLine < staticLayout.getLineCount()) {
        int startLineTop = staticLayout.getLineTop(startLine);
        int endLine = staticLayout.getLineForVertical(startLineTop + pageHeight);
        int endLineBottom = staticLayout.getLineBottom(endLine);
        int lastFullyVisibleLine;
        if(endLineBottom > startLineTop + pageHeight)
          lastFullyVisibleLine = endLine - 1;
        else
          lastFullyVisibleLine = endLine;
        int startOffset = staticLayout.getLineStart(startLine);
        int endOffset = staticLayout.getLineEnd(lastFullyVisibleLine);
        pages.add(mSpannableStringBuilder.subSequence(startOffset, endOffset));
        startLine = lastFullyVisibleLine + 1;
      }
    }

    public List<CharSequence> getPages() {
      return pages;
    }
  }
}
