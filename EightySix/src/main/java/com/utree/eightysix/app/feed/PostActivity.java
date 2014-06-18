package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.os.Bundle;
import com.utree.eightysix.app.BaseActivity;

/**
 * @author simon
 */
public class PostActivity extends BaseActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);
  }
}