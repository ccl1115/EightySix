package com.utree.eightysix.app.circle;

import android.app.Activity;
import android.os.Bundle;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;

/**
 * @author simon
 */
@Layout(R.layout.activity_my_circles)
@TopTitle(R.string.select_circle)
public class SelectCircleActivity extends MyCirclesActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}