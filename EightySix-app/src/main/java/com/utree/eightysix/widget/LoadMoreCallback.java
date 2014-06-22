package com.utree.eightysix.widget;

import android.view.View;

/**
* @author simon
*/
public interface LoadMoreCallback {

  View getLoadMoreView();

  boolean hasMore();

  boolean onLoadMoreStart();
}
