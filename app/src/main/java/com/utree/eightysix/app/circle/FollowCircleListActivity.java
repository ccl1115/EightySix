/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.circle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.data.FollowCircle;
import com.utree.eightysix.request.CircleSetRequest;
import com.utree.eightysix.response.FollowCircleListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.ThemedDialog;

/**
 */
@Layout(R.layout.activity_follow_circle_list)
@TopTitle(R.string.follow_circle)
public class FollowCircleListActivity extends BaseActivity {

  @InjectView(R.id.alv_follow_circles)
  public AdvancedListView mAlvFollowCircles;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private FollowCircleListAdapter mAdapter;
  private ThemedDialog mCircleSetDialog;

  @OnItemClick(R.id.alv_follow_circles)
  public void onAlvItemClicked(int position) {
    FeedActivity.start(this, mAdapter.getItem(position).factoryId);
  }

  @OnItemLongClick(R.id.alv_follow_circles)
  public boolean onAlvItemLongClicked(final int position) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("操作");
    builder.setItems(new String[]{
        "设置在职",
        "取消关注"
    }, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        final FollowCircle circle = mAdapter.getItem(position);
        switch (which) {
          case 0:
            showCircleSetDialog(circle);
            break;
          case 1:
            U.request("follow_circle_del", new OnResponse2<Response>() {
              @Override
              public void onResponseError(Throwable e) {

              }

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  mAdapter.remove(circle);
                }
              }
            }, Response.class, circle.factoryId);
            break;
        }
      }
    });
    builder.show();
    return true;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));

    getTopBar().getAbRight().setText("编辑");
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mAdapter != null) {
          mAdapter.toggleDelete();
        }
      }
    });

    mRstvEmpty.setDrawable(R.drawable.scene_3);
    mRstvEmpty.setText("你还没有关注任何圈子");

    showProgressBar(true);
    U.request("follow_circle_list", new OnResponse2<FollowCircleListResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FollowCircleListResponse response) {
        if (response.object.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
          mAdapter = new FollowCircleListAdapter(response.object);
          mAlvFollowCircles.setAdapter(mAdapter);
        }
        hideProgressBar();
      }
    }, FollowCircleListResponse.class);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  protected void showCircleSetDialog(final FollowCircle circle) {
    mCircleSetDialog = new ThemedDialog(this);
    mCircleSetDialog.setTitle(String.format("确认在[%s]上班么？", circle.factoryName));
    TextView textView = new TextView(this);
    textView.setText("\n请注意：" + (U.getSyncClient().getSync() != null ? U.getSyncClient().getSync().selectFactoryDays : 15) + "天之内不能修改哦\n");
    textView.setPadding(16, 16, 16, 16);
    mCircleSetDialog.setContent(textView);

    mCircleSetDialog.setPositive("设置在职", new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        request(new CircleSetRequest(circle.factoryId), new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              showToast("设置成功");
            }
          }
        }, Response.class);
      }
    });
    mCircleSetDialog.setRbNegative("重新选择", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mCircleSetDialog.dismiss();
      }
    });

    mCircleSetDialog.show();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}