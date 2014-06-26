package com.utree.eightysix.app.circle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.data.Circle;
import java.util.ArrayList;

/**
 * @author simon
 */
@TopTitle(R.string.select_circle)
public class SelectCircleActivity extends BaseCirclesActivity {

  @OnItemClick(R.id.lv_circles)
  public void onLvCirclesItemClicked(int position) {
    final Circle circle = mCircleListAdapter.getItem(position);
    if (circle != null) {
      AlertDialog dialog = new AlertDialog.Builder(this)
          .setTitle("确认在" + circle.name + "上班么？")
          .setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              circle.selected = true;
              FeedActivity.start(SelectCircleActivity.this, circle,
                  (ArrayList<Circle>) mCircleListAdapter.getCircles());
            }
          })
          .setNegativeButton("重新选择", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          }).create();

      dialog.show();
    }
  }

  private AlertDialog mConfirmSelectDialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}