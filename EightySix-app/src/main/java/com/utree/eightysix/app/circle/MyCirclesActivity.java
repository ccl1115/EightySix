package com.utree.eightysix.app.circle;

import android.app.Activity;
import android.os.Bundle;
import butterknife.OnItemClick;
import com.utree.eightysix.R;
import com.utree.eightysix.app.TopTitle;

/**
 * @author simon
 */
@TopTitle(R.string.my_circles)
public class MyCirclesActivity extends BaseCirclesActivity {

  @OnItemClick(R.id.lv_circles)
  public void onLvCirclesItemClicked(int position) {

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}