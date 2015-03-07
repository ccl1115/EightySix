/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.msg;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.chat.ChatUtils;
import com.utree.eightysix.app.chat.ConversationActivity;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
public class MsgCenterFragment extends BaseFragment {

  @InjectView(R.id.rb_count_msg)
  public RoundedButton mRbCountMsg;

  @InjectView(R.id.rb_count_chat)
  public RoundedButton mRbCountChat;

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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_msg_center, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    long unreadConversationCount = ChatUtils.ConversationUtil.getUnreadConversationCount();
    if (unreadConversationCount == 0) {
      mRbCountChat.setVisibility(View.INVISIBLE);
    } else {
      mRbCountChat.setVisibility(View.VISIBLE);
    }
    mRbCountChat.setText(String.valueOf(unreadConversationCount));
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
  public void onChatEvent(ChatEvent event) {
    if (event.getStatus() == ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT) {
      if (((Long) event.getObj()) == 0) {
        mRbCountChat.setVisibility(View.INVISIBLE);
      } else {
        mRbCountChat.setVisibility(View.VISIBLE);
      }
      mRbCountChat.setText(String.valueOf(event.getObj()));
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    U.getChatBus().register(this);

    getBaseActivity().setTopTitle("消息");
    getBaseActivity().setTopSubTitle("");

    getBaseActivity().getTopBar().getAbLeft().hide();
    getBaseActivity().getTopBar().getAbRight().hide();
  }

  @Override
  public void onDetach() {
    super.onDetach();

    U.getChatBus().unregister(this);
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);

    if (!hidden) {
      getBaseActivity().setTopTitle("消息");
      getBaseActivity().setTopSubTitle("");

      getBaseActivity().getTopBar().getAbLeft().hide();
      getBaseActivity().getTopBar().getAbRight().hide();
    }
  }
}