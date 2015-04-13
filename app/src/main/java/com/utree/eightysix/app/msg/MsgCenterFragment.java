/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.msg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.chat.*;
import com.utree.eightysix.app.friends.RequestListActivity;
import com.utree.eightysix.app.home.HomeTabActivity;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.widget.CounterView;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
public class MsgCenterFragment extends BaseFragment {

  @InjectView(R.id.rb_count_msg)
  public RoundedButton mRbCountMsg;

  @InjectView(R.id.rb_count_chat)
  public CounterView mRbCountChat;

  @InjectView(R.id.rb_count_fchat)
  public CounterView mRbCountFChat;

  @InjectView(R.id.rb_count_assist)
  public CounterView mRbCountAssist;

  @InjectView(R.id.rb_praise)
  public RoundedButton mRbPraise;

  @OnClick(R.id.ll_msg)
  public void onLlMsgClicked() {
    MsgActivity.start(getActivity(), true);
  }

  @OnClick(R.id.ll_chat)
  public void onLlChatClicked() {
    ConversationActivity.start(getActivity());
  }

  @OnClick(R.id.ll_praise)
  public void onLlPraiseClicked() {
    PraiseActivity.start(getActivity(), true);
  }

  @OnClick(R.id.ll_request)
  public void onLlFriendRequestClicked() {
    startActivity(new Intent(getActivity(), RequestListActivity.class));
  }

  @OnClick(R.id.ll_fchat)
  public void onLlFChatClicked() {
    FConversationActivity.start(getActivity());
  }

  @OnClick(R.id.ll_assist)
  public void onLlAssistClicked() {
    ChatUtils.startAssistantChat(getBaseActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_msg_center, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mRbCountChat.setCount((int) ConversationUtil.getUnreadConversationCount());
    mRbCountFChat.setCount((int) FConversationUtil.getUnreadConversationCount());
    mRbCountAssist.setCount((int) FMessageUtil.getAssistUnreadCount());
  }

  @Subscribe
  public void onNewCommentCountEvent(NewCommentCountEvent event) {
    if (event.getCount() == 0) {
      mRbCountMsg.setVisibility(View.INVISIBLE);
    } else {
      mRbCountMsg.setVisibility(View.VISIBLE);
    }

    mRbCountMsg.setText(String.valueOf(event.getCount()));
  }

  @Subscribe
  public void onHasNewPraiseEvent(HasNewPraiseEvent event) {
    if (event.has()) {
      mRbPraise.setVisibility(View.VISIBLE);
    } else {
      mRbPraise.setVisibility(View.INVISIBLE);
    }
  }

  @Subscribe
  public void onMsgCountEvent(HomeTabActivity.MsgCountEvent event) {
    switch (event.getType()) {
      case HomeTabActivity.MsgCountEvent.TYPE_ASSIST_MESSAGE_COUNT:
        mRbCountAssist.setCount(event.getCount());
        break;
      case HomeTabActivity.MsgCountEvent.TYPE_UNREAD_CONVERSATION_COUNT:
        mRbCountChat.setCount(event.getCount());
        break;
      case HomeTabActivity.MsgCountEvent.TYPE_UNREAD_FCONVERSATION_COUNT:
        mRbCountFChat.setCount(event.getCount());
        break;
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    updateTopBar();
  }

  @Override
  public void onDetach() {
    super.onDetach();

  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);

    if (!hidden) {
      updateTopBar();
    }
  }

  private void updateTopBar() {
    getBaseActivity().setTopTitle("消息");
    getBaseActivity().setTopSubTitle("");

    getBaseActivity().getTopBar().getAbLeft().hide();
    getBaseActivity().getTopBar().getAbRight().hide();
    getBaseActivity().showTopBar(true);
  }
}