package com.utree.eightysix.app.account;

import android.app.Activity;
import android.os.Bundle;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;

/**
 * @author simon
 */
@Layout(R.layout.activity_praise_static)
@TopTitle(R.string.praise_count_static)
public class PraiseStaticActivity extends BaseActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }
}