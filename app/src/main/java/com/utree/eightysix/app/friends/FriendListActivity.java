/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.AddFriendActivity;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.response.FriendListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.PinnedHeaderListView;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 */
@Layout(R.layout.activity_friend_list)
@TopTitle(R.string.my_friends)
public class FriendListActivity extends BaseActivity {

  @InjectView(R.id.plv_friends)
  public PinnedHeaderListView mAdvFriends;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  @InjectView(R.id.ll_index)
  public LinearLayout mLlIndex;

  private FriendListAdapter mFriendListAdapter;

  private SortedSet<String> mIndex = new TreeSet<String>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_add));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(FriendListActivity.this, AddFriendActivity.class));
      }
    });

    mRstvEmpty.setDrawable(R.drawable.scene_4);
    mRstvEmpty.setText("你还没有朋友");

    showProgressBar(true);
    U.request("user_friend_list", new OnResponse2<FriendListResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FriendListResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mFriendListAdapter = new FriendListAdapter(response.object);
            mAdvFriends.setAdapter(mFriendListAdapter);

            for (Friend f : response.object) {
              mIndex.add(f.initial);
            }

            int index = 0;
            for (String s : mIndex) {
              TextView textView = new TextView(FriendListActivity.this);
              textView.setText(s);
              textView.setPadding(8, 8, 8, 8);
              textView.setGravity(Gravity.CENTER);
              final int finalIndex = index;
              textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  mAdvFriends.setSelection(mFriendListAdapter.getSectionIndex(finalIndex));
                }
              });
              mLlIndex.addView(textView);
              index += 1;
            }
          }
        }
        hideProgressBar();
      }
    }, FriendListResponse.class);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}