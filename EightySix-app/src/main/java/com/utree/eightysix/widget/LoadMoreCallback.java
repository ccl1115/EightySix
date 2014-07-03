package com.utree.eightysix.widget;

import android.view.View;
import android.view.ViewGroup;

/**
* @author simon
*/
public interface LoadMoreCallback {

  View getLoadMoreView(ViewGroup parent);

  boolean hasMore();

  boolean onLoadMoreStart();
}
