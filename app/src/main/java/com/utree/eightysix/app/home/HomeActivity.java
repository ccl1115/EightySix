package com.utree.eightysix.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.annotations.SerializedName;
import com.nineoldandroids.view.ViewHelper;
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
import com.utree.eightysix.app.chat.ConversationActivity;
import com.utree.eightysix.app.chat.ConversationUtil;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.StartPublishActivityEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.app.feed.event.UploadClickedEvent;
import com.utree.eightysix.app.hometown.HometownInfoFragment;
import com.utree.eightysix.app.hometown.HometownTabFragment;
import com.utree.eightysix.app.hometown.SetHometownFragment;
import com.utree.eightysix.app.hometown.event.HometownNotSetEvent;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.msg.PraiseActivity;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.region.FactoryRegionFragment;
import com.utree.eightysix.app.region.TabRegionFragment;
import com.utree.eightysix.app.settings.MainSettingsActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout (R.layout.activity_home)
public class HomeActivity extends BaseActivity {

  private static final String FIRST_RUN_KEY = "feed";

  public  static boolean sIsRunning = false;

  @InjectView (R.id.fl_side)
  public FrameLayout mFlSide;

  @InjectView(R.id.fl_right)
  public FrameLayout mFlRight;

  @InjectView(R.id.fl_main)
  public FrameLayout mFlMain;

  @InjectView (R.id.content)
  public DrawerLayout mDlContent;

  private TabRegionFragment mTabFragment;

  private RegionFragment mRegionFragment;

  private FactoryRegionFragment mFactoryRegionFragment;

  private SetHometownFragment mSetHometownFragment;

  private HometownTabFragment mHometownTabFragment;

  private HometownInfoFragment mHometownInfoFragment;

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
  private Circle mCurrentCircle;

  private boolean mCreated;
  private int mRegionType;

  public static void start(Context context) {
    Intent intent = new Intent(context, HomeActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, int regionType) {
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("regionType", regionType);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static Intent getIntent(Context context, int tabIndex, int regionType) {
    Intent intent = new Intent(context, HomeActivity.class);

    intent.putExtra("tabIndex", tabIndex);
    intent.putExtra("regionType", regionType);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    return intent;
  }

  @Override
  public void onActionLeftClicked() {
    U.getAnalyser().trackEvent(this, "feed_title", "feed_title");
    if (mDlContent.isDrawerOpen(mFlRight)) {
      mDlContent.closeDrawer(mFlRight);
    } else if (mDlContent.isDrawerOpen(mFlSide)) {
      mDlContent.closeDrawer(mFlSide);
    } else {
      mDlContent.openDrawer(mFlSide);
    }
  }

  @Override
  public void onActionOverflowClicked() {
    if (mDlContent.isDrawerOpen(mFlSide)) {
      mDlContent.closeDrawer(mFlSide);
      mTopBar.getAbRight().setSelected(false);
    } else {
      openMenu();
      mTopBar.getAbRight().setSelected(true);
    }
  }

  @Override
  public boolean showActionOverflow() {
    return true;
  }

  @Override
  public void onTitleClicked() {
    if (mTabFragment != null && !mTabFragment.isDetached()) {
      toggleFactoryRegion();
    } else if (mHometownTabFragment != null && !mHometownTabFragment.isDetached()) {
      toggleHometownInfoFragment();
    }
    mDlContent.closeDrawer(mFlSide);
  }

  @Override
  public void onIconClicked() {
    U.getAnalyser().trackEvent(this, "feed_title", "feed_icon");
    if (mDlContent.isDrawerOpen(mFlSide)) {
      mDlContent.closeDrawer(mFlSide);
    } else {
      mDlContent.openDrawer(mFlSide);
    }
    hideFactoryRegion();
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(null);

    sIsRunning = true;

    setFillContent(true);

    ContactsSyncService.start(this, false);

    setActionLeftDrawable(getResources().getDrawable(R.drawable.tb_drawer));

    mMenuViewHolder = new MenuViewHolder(mFlRight);

    setActionAdapter();

    mTabFragment = new TabRegionFragment();
    Bundle args = new Bundle();
    args.putInt("tabIndex", getIntent().getIntExtra("tabIndex", 0));
    mTabFragment.setArguments(args);
    getSupportFragmentManager().beginTransaction().add(R.id.fl_main, mTabFragment).commit();

    mRegionFragment = (RegionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_range);

    mRegionFragment.setCallback(new RegionFragment.Callback() {
      @Override
      public void onRegionClicked(int regionType, boolean selected) {
        mDlContent.closeDrawer(mFlSide);
        if (selected) {
          mTabFragment.setRegionType(regionType);
        }
        showTabFragment();
      }

      @Override
      public void onFellowSettingClicked() {
        showSetHometownFragment(getString(R.string.set_your_hometown));
      }

      @Override
      public void onFellowClicked(boolean selected) {
        mDlContent.closeDrawer(mFlSide);
        if (selected) {
          showHometownTabFragment();
        }
      }
    });

    mDlContent.setScrimColor(0x00ffffff);

    mDlContent.setDrawerListener(new DrawerLayout.DrawerListener() {
      @Override
      public void onDrawerSlide(View drawerView, float slideOffset) {
        final int pivotY = mFlMain.getMeasuredHeight() >> 1;
        final float scale = 1 - slideOffset * 0.1f;

        if (drawerView.getId() == R.id.fl_side) {
          final int measuredWidth = mFlSide.getMeasuredWidth();
          ViewHelper.setTranslationX(mFlMain, measuredWidth * slideOffset);
          ViewHelper.setPivotX(mFlMain, 0f);
          ViewHelper.setPivotY(mFlMain, pivotY);
          ViewHelper.setScaleX(mFlMain, scale);
          ViewHelper.setScaleY(mFlMain, scale);

          ViewHelper.setTranslationX(mTopBar, measuredWidth * slideOffset);
          ViewHelper.setPivotX(mTopBar, 0f);
          ViewHelper.setPivotY(mTopBar, pivotY + mTopBar.getMeasuredHeight());
          ViewHelper.setScaleX(mTopBar, scale);
          ViewHelper.setScaleY(mTopBar, scale);

          float scale2 = 0.7f + slideOffset * 0.3f;
          ViewHelper.setPivotX(mFlSide, measuredWidth);
          ViewHelper.setPivotY(mFlSide, mFlSide.getMeasuredHeight() >> 1);
          ViewHelper.setScaleX(mFlSide, scale2);
          ViewHelper.setScaleY(mFlSide, scale2);
          ViewHelper.setAlpha(mFlSide, slideOffset * slideOffset);
        } else {
          int measuredWidth = mFlRight.getMeasuredWidth();
          ViewHelper.setTranslationX(mFlMain, -measuredWidth * slideOffset);
          ViewHelper.setPivotX(mFlMain, mFlMain.getMeasuredWidth());
          ViewHelper.setPivotY(mFlMain, pivotY);
          ViewHelper.setScaleX(mFlMain, scale);
          ViewHelper.setScaleY(mFlMain, scale);

          ViewHelper.setTranslationX(mTopBar, -measuredWidth * slideOffset);
          ViewHelper.setPivotX(mTopBar, mFlMain.getMeasuredWidth());
          ViewHelper.setPivotY(mTopBar, pivotY + mTopBar.getMeasuredHeight());
          ViewHelper.setScaleX(mTopBar, scale);
          ViewHelper.setScaleY(mTopBar, scale);

          float scale2 = 0.7f + slideOffset * 0.3f;
          ViewHelper.setPivotY(mFlRight, mFlRight.getMeasuredHeight() >> 1);
          ViewHelper.setScaleX(mFlRight, scale2);
          ViewHelper.setScaleY(mFlRight, scale2);
          ViewHelper.setAlpha(mFlRight, slideOffset * slideOffset);
        }
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        if (drawerView.getId() == R.id.fl_side) {
          mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mFlRight);
        } else {
          mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mFlSide);
          mTopBar.getAbRight().setSelected(true);
        }
      }

      @Override
      public void onDrawerClosed(View drawerView) {
        mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mFlSide);
        mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mFlRight);
        mTopBar.getAbRight().setSelected(false);

        ViewHelper.setTranslationX(mFlMain, 0);
        ViewHelper.setPivotX(mFlMain, 0);
        ViewHelper.setPivotY(mFlMain, 0);
        ViewHelper.setScaleX(mFlMain, 1);
        ViewHelper.setScaleY(mFlMain, 1);

        ViewHelper.setTranslationX(mTopBar, 0);
        ViewHelper.setPivotX(mTopBar, 0);
        ViewHelper.setPivotY(mTopBar, 0);
        ViewHelper.setScaleX(mTopBar, 1);
        ViewHelper.setScaleY(mTopBar, 1);
      }

      @Override
      public void onDrawerStateChanged(int newState) {

      }
    });

    U.getChatBus().register(this);

    (new AsyncTask<Void, Void, Long>() {
      @Override
      protected Long doInBackground(Void... voids) {
        return ConversationUtil.getUnreadConversationCount();
      }

      @Override
      protected void onPostExecute(Long aLong) {
        mTopBar.getActionView(0).setCount(aLong.intValue());
      }
    }).execute();

    mCreated = true;

    onNewIntent(getIntent());
  }

  @Override
  protected void onDestroy() {
    Env.setFirstRun(FIRST_RUN_KEY, false);
    U.getChatBus().unregister(this);
    sIsRunning = false;
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    if (mResumed && getCount() > U.getConfigInt("activity.background.refresh.time")) {
      if (mTabFragment != null && !mTabFragment.isDetached()) {
        mTabFragment.setRegionType(mTabFragment.getRegionType());
      } else if (mHometownTabFragment != null && !mHometownTabFragment.isDetached()) {
        mHometownTabFragment.refresh();
      }
    }
    super.onResume();

    Sync sync = U.getSyncClient().getSync();
    if (sync != null && sync.upgrade != null) {
      int v = 0;
      try {
        v = Integer.parseInt(sync.upgrade.version);
      } catch (NumberFormatException ignored) {
      }
      if (v > C.VERSION) {
        getTopBar().getActionOverflow().setHasNew(0, true);
        mMenuViewHolder.mRbSettingsDot.setVisibility(View.VISIBLE);
      } else {
        getTopBar().getActionOverflow().setHasNew(0, false);
        mMenuViewHolder.mRbSettingsDot.setVisibility(View.INVISIBLE);
      }
    }

  }

  @Subscribe
  public void onChatEvent(ChatEvent event) {
    if (event.getStatus() == ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT) {
      mTopBar.getActionView(0).setCount(((Long) event.getObj()).intValue());
    }
  }

  @Subscribe
  public void onHometownNotSetEvent(HometownNotSetEvent event) {
    mDlContent.openDrawer(mFlSide);
    showSetHometownFragment(getString(R.string.set_hometown_tip));
  }


  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onNewCommentCountEvent(NewCommentCountEvent event) {
    getTopBar().getActionView(1).setCount(event.getCount());
  }

  @Subscribe
  public void onHasNewPraiseEvent(HasNewPraiseEvent event) {
    getTopBar().getActionOverflow().setHasNew(1, event.has());
    if (event.has()) {
      mMenuViewHolder.mRbNewPraiseDot.setVisibility(View.VISIBLE);
    } else {
      mMenuViewHolder.mRbNewPraiseDot.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU) {
      if (mDlContent.isDrawerOpen(mFlSide)) {
        mDlContent.closeDrawer(mFlSide);
      } else {
        mDlContent.openDrawer(mFlSide);
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (mFactoryRegionFragment != null && mFactoryRegionFragment.onBackPressed()) {
        return true;
      }

      if (mShouldExit) {
        finish();
        return true;
      } else {
        mShouldExit = true;
        showToast(getString(R.string.press_again_to_exit));
        getHandler().postDelayed(new Runnable() {
          @Override
          public void run() {
            mShouldExit = false;
          }
        }, 1000);
        return true;
      }
    }
    return super.onKeyUp(keyCode, event);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    if (mCreated) {
      showTabFragment();
    }

    final int regionType = intent.getIntExtra("regionType", Account.inst().getLastRegionType());
    final int tabIndex = intent.getIntExtra("tabIndex", 0);

    if (regionType != -1 && regionType != mRegionType) {
      mTabFragment.setRegionType(regionType);
      mTabFragment.setTabIndex(tabIndex);
    }

    mRegionType = regionType;

    setHasNewPraise();
    setNewCommentCount();

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mDlContent.closeDrawer(mFlSide);
        mDlContent.closeDrawer(mFlRight);
      }
    }, 1000);
  }

  @Override
  protected void onStart() {
    super.onStart();

    startService(new Intent(this, FetchNotificationService.class));
  }

  @Override
  protected void onStop() {
    super.onStop();

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
    if (!mTabFragment.isDetached()) {
      if (!mTabFragment.canPublish()) {
        showNoPermDialog();
      } else {
        PublishActivity.start(this, -1, null);
      }
    } else if (!mHometownTabFragment.isDetached()) {
      PublishActivity.startHometown(this);
    }
  }

  @Subscribe
  public void onCurrentCircleResponseEvent(CurrentCircleResponseEvent event) {
    mCurrentCircle = event.getCircle();
  }

  private void hideFactoryRegion() {
    if (mFactoryRegionFragment != null) {
      mFactoryRegionFragment.detachSelf();
    }
  }

  private void toggleFactoryRegion() {
    if (mFactoryRegionFragment == null) {
      mFactoryRegionFragment = new FactoryRegionFragment();

      mFactoryRegionFragment.setRegionType(mRegionFragment.getRegionType());
      getSupportFragmentManager().beginTransaction()
          .add(R.id.fl_main, mFactoryRegionFragment).commit();
    } else if (mFactoryRegionFragment.isDetached()) {
      getSupportFragmentManager().beginTransaction()
          .attach(mFactoryRegionFragment).commit();
      mFactoryRegionFragment.setRegionType(mRegionFragment.getRegionType());
    } else {
      mFactoryRegionFragment.detachSelf();
    }
  }

  private void hideHometownInfoFragment() {
    if (mHometownInfoFragment != null) {
      mHometownInfoFragment.detachSelf();
    }
  }

  private void toggleHometownInfoFragment() {
    if (mHometownInfoFragment == null) {
      mHometownInfoFragment = new HometownInfoFragment();

      mHometownInfoFragment.setCallback(new HometownInfoFragment.Callback() {
        @Override
        public void onHometownClicked(int hometownId, int hometownType, String hometownName) {
          if (mHometownTabFragment != null) {
            mHometownTabFragment.setHometown(hometownId, hometownType, hometownName);
          }
        }
      });

      getSupportFragmentManager().beginTransaction()
          .add(R.id.fl_main, mHometownInfoFragment)
          .commit();
    } else if (mHometownInfoFragment.isDetached()) {
      getSupportFragmentManager().beginTransaction()
          .attach(mHometownInfoFragment).commit();
    } else {
      mHometownInfoFragment.detachSelf();
    }
  }

  private void showSetHometownFragment(String title) {
    if (mSetHometownFragment == null) {
      mSetHometownFragment = new SetHometownFragment();
      Bundle args = new Bundle();
      args.putString("title", title);
      mSetHometownFragment.setArguments(args);

      mSetHometownFragment.setCallback(new SetHometownFragment.Callback() {
        @Override
        public void onHometownSet(int hometownId) {
          if (mHometownTabFragment != null) {
            mHometownTabFragment.setHometown(hometownId, -1, "");
          }
        }
      });

      getSupportFragmentManager().beginTransaction()
          .add(android.R.id.content, mSetHometownFragment).commit();
    } else if (mSetHometownFragment.isDetached()) {
      Bundle args = mSetHometownFragment.getArguments();
      args.putString("title", title);
      getSupportFragmentManager().beginTransaction()
          .attach(mSetHometownFragment)
          .commit();
    }
  }

  private void showHometownTabFragment() {
    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
    if (mHometownTabFragment == null) {
      mHometownTabFragment = new HometownTabFragment();

      t.add(R.id.fl_main, mHometownTabFragment);
    } else if (mHometownTabFragment.isDetached()) {
      t.attach(mHometownTabFragment);
    }
    if (mTabFragment != null && !mTabFragment.isDetached()) {
      t.detach(mTabFragment);
    }
    t.commitAllowingStateLoss();
  }

  private void showTabFragment() {
    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
    if (mTabFragment == null) {
      mTabFragment = new TabRegionFragment();
      Bundle args = new Bundle();
      args.putInt("tabIndex", getIntent().getIntExtra("tabIndex", 0));
      mTabFragment.setArguments(args);

      t.add(R.id.fl_main, mTabFragment);
    } else if (mTabFragment.isDetached()) {
      t.attach(mTabFragment);
    }

    if (mHometownTabFragment != null && !mHometownTabFragment.isDetached()) {
      t.detach(mHometownTabFragment);
    }
    t.commitAllowingStateLoss();
  }

  private void openMenu() {
    U.getAnalyser().trackEvent(this, "feed_more", "feed_more");
    if (mDlContent.isDrawerOpen(mFlRight)) {
      mDlContent.closeDrawer(mFlRight);
    } else {
      mDlContent.openDrawer(mFlRight);
    }
    hideFactoryRegion();
  }

  private void setActionAdapter() {
    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return null;
      }

      @Override
      public Drawable getIcon(int position) {
        if (position == 1) {
          return getResources().getDrawable(R.drawable.ic_action_msg);
        } else if (position == 0) {
          return getResources().getDrawable(R.drawable.ic_chat_large);
        }
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
      }

      @Override
      public void onClick(View view, int position) {
        if (position == 1) {
          U.getAnalyser().trackEvent(HomeActivity.this, "feed_msg", "feed_msg");
          MsgActivity.start(HomeActivity.this, Account.inst().getNewCommentCount() > 0);
        } else if (position == 0) {
          U.getAnalyser().trackEvent(HomeActivity.this, "feed_converstaion", "feed_conversation");
          ConversationActivity.start(HomeActivity.this);
        }
      }

      @Override
      public int getCount() {
        return 2;
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
      mInviteDialog = U.getShareManager().shareAppDialog(this, mCurrentCircle);
    }
    if (!mInviteDialog.isShowing()) {
      mInviteDialog.show();
    }
  }

  private void setHasNewPraise() {
    mMenuViewHolder.mRbNewPraiseDot.setVisibility(Account.inst().getHasNewPraise() ? View.VISIBLE : View.INVISIBLE);
    getTopBar().getActionOverflow().setHasNew(1, Account.inst().getHasNewPraise());
  }

  private void setNewCommentCount() {
    getTopBar().getActionView(1).setCount(Account.inst().getNewCommentCount());
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

    @OnClick (R.id.rl_my_friends)
    void onLlMyFriendsClicked() {
      startActivity(new Intent(HomeActivity.this, AccountActivity.class));
    }

    @OnClick (R.id.rb_add)
    void onRbAddClicked() {
      startActivity(new Intent(HomeActivity.this, AddFriendActivity.class));
    }

    @OnClick (R.id.ll_invite)
    void onLlInviteClicked() {
      showInviteDialog();
    }

    @OnClick (R.id.rl_praise_count)
    void onLlPraiseCountClicked() {
      PraiseActivity.start(HomeActivity.this, Account.inst().getHasNewPraise());
    }

    @OnClick (R.id.ll_settings)
    void onLlSettingsClicked() {
      startActivity(new Intent(HomeActivity.this, MainSettingsActivity.class));
    }

    @OnClick (R.id.rl_topic_list)
    void onLlTopicListClicked() {
      TopicListActivity.start(HomeActivity.this);
    }

    @OnClick(R.id.rl_invite_code)
    void onRlInviteCodeClicked() {


    }
  }

  @Keep
  public static class GetInviteCode {
    @SerializedName("msg")
    public String msg;

    @SerializedName("newCount")
    public int newCount;

    @SerializedName("inviteCode")
    public String inviteCode;
  }
}
