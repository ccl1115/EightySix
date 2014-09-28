package com.utree.eightysix.app.chat;

/**
 * @author simon
 */
//@Layout(R.layout.activity_chat)
//public class ChatActivity extends BaseActivity {
//
//  @InjectView(R.id.fl_send)
//  public FrameLayout mFlSend;
//
//  @InjectView(R.id.alv_chats)
//  public AdvancedListView mAlvChats;
//
//  private ChatAdapter mChatAdapter;
//
//  private String mUsername;
//
//  public static void start(Context context, String username) {
//    Intent intent = new Intent(context, ChatActivity.class);
//    intent.putExtra("username", username);
//
//    if (!(context instanceof Activity)) {
//      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    }
//
//    context.startActivity(intent);
//  }
//
//  @Override
//  public void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//
//    mChatAdapter = new ChatAdapter();
//
//    mUsername = getIntent().getStringExtra("username");
//
//    if (mUsername == null) {
//      finish();
//    }
//
//    EMConversation conversation = EMChatManager.getInstance().getConversation(mUsername);
//
//    mChatAdapter.add(conversation.getAllMessages());
//  }
//
//  @Subscribe
//  public void onChatStatusEvent(ChatStatusEvent event) {
//
//  }
//
//  @Override
//  public void onLogout(Account.LogoutEvent event) {
//    finish();
//  }
//
//  @Override
//  public void onActionLeftClicked() {
//    finish();
//  }
//}