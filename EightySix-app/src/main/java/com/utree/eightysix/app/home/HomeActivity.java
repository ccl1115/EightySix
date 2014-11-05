package com.utree.eightysix.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.AccountActivity;
import com.utree.eightysix.app.account.AddFriendActivity;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.StartPublishActivityEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.app.feed.event.UploadClickedEvent;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.msg.PraiseActivity;
import com.utree.eightysix.app.publish.FeedbackActivity;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.region.FactoryRegionFragment;
import com.utree.eightysix.app.region.TabRegionFragment;
import com.utree.eightysix.app.settings.HelpActivity;
import com.utree.eightysix.app.settings.MainSettingsActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TopBar;
import de.akquinet.android.androlog.Log;

/**
 */
@Layout (R.layout.activity_home)
public class HomeActivity extends BaseActivity {

  private static final String FIRST_RUN_KEY = "feed";

  @InjectView (R.id.ib_send)
  public ImageButton mSend;

  @InjectView (R.id.ll_side)
  public LinearLayout mLlSide;

  @InjectView (R.id.content)
  public DrawerLayout mDlContent;

  private PopupWindow mPopupMenu;

  private TabRegionFragment mTabFragment;

  private RegionFragment mRegionFragment;

  private FactoryRegionFragment mFactoryRegionFragment;

  /**
   * 邀请好友对话框
   */
  private ThemedDialog mInviteDialog;

  /**
   * 没有发帖权限对话框
   */
  private ThemedDialog mNoPermDialog;

  private MenuViewHolder mMenuViewHolder;
  private ThemedDialog mUnlockDialog;

  private boolean mShouldExit;

  public static void start(Context context) {
    Intent intent = new Intent(context, HomeActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("circle", circle);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle, boolean skipCache) {
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("circle", circle);
    intent.putExtra("skipCache", skipCache);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, int id) {
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("id", id);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, int id, boolean skipCache) {
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("id", id);
    intent.putExtra("skipCache", skipCache);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static Intent getIntent(Context context) {
    Intent intent = new Intent(context, HomeActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    return intent;
  }

  @OnClick (R.id.ib_send)
  public void onIbSendClicked() {
    U.getAnalyser().trackEvent(this, "feed_publish", "feed_publish");
    if (!mTabFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, -1, null);
    }
  }

  @Override
  public void onActionLeftClicked() {
    U.getAnalyser().trackEvent(this, "feed_title", "feed_title");
    if (mDlContent.isDrawerOpen(mLlSide)) {
      mDlContent.closeDrawer(mLlSide);
    } else {
      mDlContent.openDrawer(mLlSide);
    }
  }

  @Override
  public void onActionOverflowClicked() {
    openMenu();
  }

  @Override
  public boolean showActionOverflow() {
    return true;
  }

  @Override
  public void onTitleClicked() {
    toggleFactoryRegion();
    mDlContent.closeDrawer(mLlSide);
  }

  @Override
  public void onIconClicked() {
    U.getAnalyser().trackEvent(this, "feed_title", "feed_icon");
    if (mDlContent.isDrawerOpen(mLlSide)) {
      mDlContent.closeDrawer(mLlSide);
    } else {
      mDlContent.openDrawer(mLlSide);
    }
    hideFactoryRegion();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ContactsSyncService.start(this, false);

    setActionLeftDrawable(getResources().getDrawable(R.drawable.ic_drawer));

    if (mPopupMenu == null) {
      LinearLayout menu = (LinearLayout) View.inflate(HomeActivity.this, R.layout.widget_feed_menu, null);
      mPopupMenu = new PopupWindow(menu, dp2px(190), dp2px(315) + 6);
      mMenuViewHolder = new MenuViewHolder(menu);
      mPopupMenu.setFocusable(true);
      mPopupMenu.setIgnoreCheekPress();
      mPopupMenu.setBackgroundDrawable(new BitmapDrawable(getResources()));
    }

    setActionAdapter();

    mTabFragment = new TabRegionFragment();
    getSupportFragmentManager().beginTransaction().add(R.id.fl_feed, mTabFragment, "tab").commit();

    mRegionFragment = (RegionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_range);

    mRegionFragment.setCallback(new RegionFragment.Callback() {
      @Override
      public void onItemClicked(int regionType, boolean selected) {
        mDlContent.closeDrawer(mLlSide);
        if (selected) {
          mTabFragment.setRegionType(regionType);
        }
      }
    });

    onNewIntent(getIntent());
  }

  @Override
  protected void onDestroy() {
    Env.setFirstRun(FIRST_RUN_KEY, false);
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();

    Sync sync = U.getSyncClient().getSync();
    if (sync != null && sync.upgrade != null) {
      int v = 0;
      try {
        v = Integer.parseInt(sync.upgrade.version);
      } catch (NumberFormatException ignored) {
      }
      if (v > C.VERSION) {
        getTopBar().getActionOverflow().setHasNew(true);
        mMenuViewHolder.mRbSettingsDot.setVisibility(View.VISIBLE);
      } else {
        getTopBar().getActionOverflow().setHasNew(false);
        mMenuViewHolder.mRbSettingsDot.setVisibility(View.INVISIBLE);
      }
    }

  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onNewCommentCountEvent(NewCommentCountEvent event) {
    getTopBar().getActionView(0).setCount(event.getCount());
  }

  @Subscribe
  public void onHasNewPraiseEvent(HasNewPraiseEvent event) {
    getTopBar().getActionOverflow().setHasNew(event.has());
    if (event.has()) {
      mMenuViewHolder.mRbNewPraiseDot.setVisibility(View.VISIBLE);
    } else {
      mMenuViewHolder.mRbNewPraiseDot.setVisibility(View.INVISIBLE);
    }
  }

  @Subscribe
  public void onSyncEvent(Sync sync) {
    setActionAdapter();
  }

  @Override
  public void onBackPressed() {
    if (mTabFragment != null && mTabFragment.onBackPressed()) {
      return;
    }


    if (mShouldExit) {
      finish();
    } else {
      mShouldExit = true;
      showToast("再按一次返回键退出");
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          mShouldExit = false;
        }
      }, 1000);
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU) {
      openMenu();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onNewIntent(Intent intent) {

    setHasNewPraise();
    setNewCommentCount();
  }

  @Override
  protected void onStart() {
    super.onStart();

    startService(new Intent(this, FetchNotificationService.class));
  }

  @Override
  protected void onStop() {
    Log.d("FeedActivity", "onStop");
    super.onStop();
    mDlContent.closeDrawer(mLlSide);

    stopService(new Intent(this, FetchNotificationService.class));
  }

  @Subscribe
  public void onUploadClicked(UploadClickedEvent event) {
    ContactsSyncService.start(this, true);
    showProgressBar(true);
  }

  @Subscribe
  public void onUnlockClicked(UnlockClickedEvent event) {
    showUnlockDialog();
    ContactsSyncService.start(this, true);
  }

  @Subscribe
  public void onInviteClicked(InviteClickedEvent event) {
    showInviteDialog();
  }

  @Subscribe
  public void onStartPublishActivity(StartPublishActivityEvent event) {
    if (!mTabFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, -1, null);
    }
  }

  public void setTitle(Circle circle) {
    if (circle == null) return;

    setTopTitle(circle.shortName);
    if (circle.lock == 1) {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_lock_small), null, null, null);
      getTopBar().mSubTitle.setCompoundDrawablePadding(U.dp2px(5));
    } else {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
  }

  private void showFactoryRegion() {
    if (mFactoryRegionFragment == null) {
      mFactoryRegionFragment = new FactoryRegionFragment();

      getSupportFragmentManager().beginTransaction()
          .add(R.id.content, mFactoryRegionFragment).commit();
    } else if (mFactoryRegionFragment.isDetached()) {
      getSupportFragmentManager().beginTransaction()
          .attach(mFactoryRegionFragment).commit();
    }

    mFactoryRegionFragment.setRegionType(mRegionFragment.getRegionType());
  }

  private void hideFactoryRegion() {
    if (mFactoryRegionFragment != null) {
      if (!mFactoryRegionFragment.isDetached()) {
        getSupportFragmentManager().beginTransaction()
            .detach(mFactoryRegionFragment).commit();
      }
    }
  }

  private void toggleFactoryRegion() {
    if (mFactoryRegionFragment == null) {
      showFactoryRegion();
    } else if (mFactoryRegionFragment.isDetached()) {
      getSupportFragmentManager().beginTransaction()
          .attach(mFactoryRegionFragment).commit();
      mFactoryRegionFragment.setRegionType(mRegionFragment.getRegionType());
    } else {
      getSupportFragmentManager().beginTransaction()
          .detach(mFactoryRegionFragment).commit();
    }
  }

  private void openMenu() {
    U.getAnalyser().trackEvent(this, "feed_more", "feed_more");
    if (mPopupMenu.isShowing()) {
      mPopupMenu.dismiss();
    } else {
      mPopupMenu.showAsDropDown(getTopBar().mActionOverFlow);
      mDlContent.closeDrawer(mLlSide);
    }
  }

  private void setActionAdapter() {
    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return null;
      }

      @Override
      public Drawable getIcon(int position) {
        if (position == 0) {
          return getResources().getDrawable(R.drawable.ic_action_msg);
        }
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
      }

      @Override
      public void onClick(View view, int position) {
        if (position == 0) {
          U.getAnalyser().trackEvent(HomeActivity.this, "feed_msg", "feed_msg");
          MsgActivity.start(HomeActivity.this, Account.inst().getNewCommentCount() > 0);
        }
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }
    });
  }

  private void showNoPermDialog() {
    if (mNoPermDialog == null) {
      mNoPermDialog = new ThemedDialog(this);
      View view = LayoutInflater.from(this).inflate(R.layout.dialog_publish_locked, null);
      NoPermViewHolder noPermViewHolder = new NoPermViewHolder(view);
      String tip = getString(R.string.no_perm_tip);
      int index = tip.indexOf("解锁条件");
      ForegroundColorSpan span = new ForegroundColorSpan(
          getResources().getColor(R.color.apptheme_primary_light_color));
      SpannableString spannableString = new SpannableString(tip);
      spannableString.setSpan(span, index, index + 4, 0);
      noPermViewHolder.mTvNoPermTip.setText(spannableString);
      mNoPermDialog.setContent(view);
      mNoPermDialog.setPositive(R.string.invite_people, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mNoPermDialog.dismiss();
          showInviteDialog();
        }
      });
      mNoPermDialog.setTitle(getString(R.string.no_perm_to_publish));
    }

    if (!mNoPermDialog.isShowing()) {
      mNoPermDialog.show();
    }
  }

  private void showUnlockDialog() {
    if (mUnlockDialog == null) {
      mUnlockDialog = new ThemedDialog(this);
      View view = LayoutInflater.from(this).inflate(R.layout.dialog_unlock, null);
      TextView tipView = (TextView) view.findViewById(R.id.tv_unlock_tip);
      String tip = getString(R.string.unlock_tip, U.getSyncClient().getSync().unlockFriends, U.getSyncClient().getSync().unlockFriends);
      int index = tip.indexOf("解锁条件");
      ForegroundColorSpan span = new ForegroundColorSpan(
          getResources().getColor(R.color.apptheme_primary_light_color));
      SpannableString spannableString = new SpannableString(tip);
      spannableString.setSpan(span, index, index + 4, 0);
      tipView.setText(spannableString);
      mUnlockDialog.setContent(view);
      mUnlockDialog.setPositive(R.string.invite_people, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mUnlockDialog.dismiss();
          showInviteDialog();
        }
      });
      mUnlockDialog.setTitle("秘密为什么会隐藏");
    }

    if (!mUnlockDialog.isShowing()) {
      mUnlockDialog.show();
    }
  }

  private void showInviteDialog() {
    if (mInviteDialog == null) {
      //mInviteDialog = U.getShareManager().shareAppDialog(this, mTabFragment.getCircle());
    }
    if (!mInviteDialog.isShowing()) {
      mInviteDialog.show();
    }
  }

  private void setHasNewPraise() {
    mMenuViewHolder.mRbNewPraiseDot.setVisibility(Account.inst().getHasNewPraise() ? View.VISIBLE : View.INVISIBLE);
    getTopBar().getActionOverflow().setHasNew(Account.inst().getHasNewPraise());
  }

  private void setNewCommentCount() {
    getTopBar().getActionView(0).setCount(Account.inst().getNewCommentCount());
  }

  @Keep
  class NoPermViewHolder {

    @InjectView (R.id.tv_no_perm_tip)
    TextView mTvNoPermTip;

    NoPermViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  @Keep
  class MenuViewHolder {

    @InjectView (R.id.tv_praise_count)
    TextView mTvPraiseCount;

    @InjectView (R.id.rb_new_praise_dot)
    RoundedButton mRbNewPraiseDot;

    @InjectView (R.id.rb_settings_dot)
    RoundedButton mRbSettingsDot;

    MenuViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick (R.id.ll_my_friends)
    void onLlMyFriendsClicked() {
      startActivity(new Intent(HomeActivity.this, AccountActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.rb_add)
    void onRbAddClicked() {
      startActivity(new Intent(HomeActivity.this, AddFriendActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_invite)
    void onLlInviteClicked() {
      showInviteDialog();
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_praise_count)
    void onLlPraiseCountClicked() {
      PraiseActivity.start(HomeActivity.this, Account.inst().getHasNewPraise());
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_feedback)
    void onLlFeedbackClicked() {
      FeedbackActivity.start(HomeActivity.this);
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_settings)
    void onLlSettingsClicked() {
      startActivity(new Intent(HomeActivity.this, MainSettingsActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_help)
    void onLlHelpClicked() {
      startActivity(new Intent(HomeActivity.this, HelpActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_topic_list)
    void onLlTopicListClicked() {
      TopicListActivity.start(HomeActivity.this);
      mPopupMenu.dismiss();
    }
  }

}
