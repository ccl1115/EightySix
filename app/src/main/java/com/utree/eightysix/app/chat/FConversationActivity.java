/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.List;

/**
 */
@Layout(R.layout.activity_f_conversation)
@TopTitle(R.string.chat)
public class FConversationActivity extends BaseActivity {

  public static final int PAGE_SIZE = 20;

  public static void start(Context context) {
    Intent intent = new Intent(context, FConversationActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.alv_conversation)
  public AdvancedListView mAlvConversation;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  @OnItemClick(R.id.alv_conversation)
  public void onAlvConversationItemClicked(int position) {
    FriendConversation item = mAdapter.getItem(position);

    ChatUtils.startFriendChat(this, item.getViewId());
  }

  private FConversationAdapter mAdapter;

  private int mPage = 0;
  private boolean mHasMore;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_action_overflow));

    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showMenuDialog();
      }
    });

    mAlvConversation.setEmptyView(mRstvEmpty);

    final List<FriendConversation> conversations = FConversationUtil.getConversations(mPage, PAGE_SIZE);

    mRstvEmpty.setVisibility(View.GONE);
    mAdapter = new FConversationAdapter(conversations);
    mAlvConversation.setAdapter(mAdapter);
    U.getChatBus().register(mAdapter);

    mHasMore = conversations.size() == PAGE_SIZE;

    mAlvConversation.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mHasMore;
      }

      @Override
      public boolean onLoadMoreStart() {
        mPage += 1;
        List<FriendConversation> fc = FConversationUtil.getConversations(mPage, PAGE_SIZE);
        mAdapter.add(fc);
        mHasMore = fc.size() == PAGE_SIZE;
        return true;
      }
    });

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mAdapter != null) {
      U.getChatBus().unregister(mAdapter);
    }
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

  private void showMenuDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setItems(new String[]{"忽略所有未读", "清空会话列表"},
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case 0:
                FMessageUtil.setAllRead();
                break;
              case 1:
                FConversationUtil.deleteAllConversation();
                break;
            }
          }
        });
    builder.show();
  }
}