package com.utree.eightysix.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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

  @InjectView (R.id.ib_send)
  public ImageButton mSend;

  @InjectView (R.id.fl_side)
  public FrameLayout mFlSide;

  @InjectView(R.id.fl_right)
  public FrameLayout mFlRight;

  @InjectView (R.id.content)
  public DrawerLayout mDlContent;

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
  private Circle mCurrentCircle;

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
    if (mDlContent.isDrawerOpen(mFlSide)) {
      mDlContent.closeDrawer(mFlSide);
    } else {
      mDlContent.openDrawer(mFlSide);
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);

    setFillContent(true);

    ContactsSyncService.start(this, false);

    setActionLeftDrawable(getResources().getDrawable(R.drawable.ic_drawer));

    mMenuViewHolder = new MenuViewHolder(mFlRight);

    setActionAdapter();

    mTabFragment = new TabRegionFragment();
    Bundle args = new Bundle();
    args.putInt("tabIndex", getIntent().getIntExtra("tabIndex", 0));
    mTabFragment.setArguments(args);
    getSupportFragmentManager().beginTransaction().add(R.id.fl_feed, mTabFragment).commit();

    mRegionFragment = (RegionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_range);

    mRegionFragment.setCallback(new RegionFragment.Callback() {
      @Override
      public void onItemClicked(int regionType, boolean selected) {
        mDlContent.closeDrawer(mFlSide);
        if (selected) {
          mTabFragment.setRegionType(regionType);
        }
      }
    });

    mDlContent.setScrimColor(0x00ffffff);

    mDlContent.setDrawerListener(new DrawerLayout.DrawerListener() {
      @Override
      public void onDrawerSlide(View drawerView, float slideOffset) {
        int measuredWidth = mFlSide.getMeasuredWidth();
        View view = mTabFragment.getView();
        int topBarHeight = mTopBar.getMeasuredHeight();

        int pivotY = (view.getMeasuredHeight() - topBarHeight) >> 1;
        float scale = 1 - slideOffset * 0.1f;
        if (drawerView.getId() == R.id.fl_side) {
          ViewHelper.setTranslationX(view, measuredWidth * slideOffset);
          ViewHelper.setPivotX(view, 0f);
          ViewHelper.setPivotY(view, pivotY);
          ViewHelper.setScaleX(view, scale);
          ViewHelper.setScaleY(view, scale);

          ViewHelper.setTranslationX(mTopBar, measuredWidth * slideOffset);
          ViewHelper.setPivotX(mTopBar, 0f);
          ViewHelper.setPivotY(mTopBar, pivotY + topBarHeight);
          ViewHelper.setScaleX(mTopBar, scale);
          ViewHelper.setScaleY(mTopBar, scale);
        } else {
          ViewHelper.setTranslationX(view, - mFlRight.getMeasuredWidth() * slideOffset);
          ViewHelper.setPivotX(view, view.getMeasuredWidth());
          ViewHelper.setPivotY(view, pivotY);
          ViewHelper.setScaleX(view, scale);
          ViewHelper.setScaleY(view, scale);

          ViewHelper.setTranslationX(mTopBar, - mFlRight.getMeasuredWidth() * slideOffset);
          ViewHelper.setPivotX(mTopBar, view.getMeasuredWidth());
          ViewHelper.setPivotY(mTopBar, pivotY + topBarHeight);
          ViewHelper.setScaleX(mTopBar, scale);
          ViewHelper.setScaleY(mTopBar, scale);
        }

        ViewHelper.setTranslationY(mSend, U.dp2px(100) * slideOffset);
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        if (drawerView.getId() == R.id.fl_side) {
          mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mFlRight);
        } else {
          mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mFlSide);
        }
      }

      @Override
      public void onDrawerClosed(View drawerView) {
        mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mFlSide);
        mDlContent.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mFlRight);
      }

      @Override
      public void onDrawerStateChanged(int newState) {

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
    if (mResumed && getCount() > U.getConfigInt("activity.background.refresh.time")
        && mTabFragment != null) {
      mTabFragment.setRegionType(mTabFragment.getRegionType());
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
        getTopBar().getActionOverflow().setHasNew(true);
        mMenuViewHolder.mRbSettingsDot.setVisibility(View.VISIBLE);
      } else {
        getTopBar().getActionOverflow().setHasNew(false);
        mMenuViewHolder.mRbSettingsDot.setVisibility(View.INVISIBLE);
      }
    }

    mDlContent.closeDrawer(mFlSide);
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

    if (mFactoryRegionFragment != null && mFactoryRegionFragment.onBackPressed()) {
      return;
    }


    if (mShouldExit) {
      finish();
    } else {
      mShouldExit = true;
      showToast(getString(R.string.press_again_to_exit));
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

    final int regionType = intent.getIntExtra("regionType", Account.inst().getLastRegionType());
    final int tabIndex = intent.getIntExtra("tabIndex", 0);

    if (regionType != -1) {
      mTabFragment.setRegionType(regionType);
    }

    mTabFragment.setTabIndex(tabIndex);

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
    if (!mTabFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, -1, null);
    }
  }

  @Subscribe
  public void onCurrentCircleResponseEvent(CurrentCircleResponseEvent event) {
    mCurrentCircle = event.getCircle();
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
      mInviteDialog = U.getShareManager().shareAppDialog(this, mCurrentCircle);
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

    @OnClick (R.id.rl_my_friends)
    void onLlMyFriendsClicked() {
      startActivity(new Intent(HomeActivity.this, AccountActivity.class));
      mDlContent.closeDrawer(mFlRight);
    }

    @OnClick (R.id.rb_add)
    void onRbAddClicked() {
      startActivity(new Intent(HomeActivity.this, AddFriendActivity.class));
      mDlContent.closeDrawer(mFlRight);
    }

    @OnClick (R.id.ll_invite)
    void onLlInviteClicked() {
      showInviteDialog();
      mDlContent.closeDrawer(mFlRight);
    }

    @OnClick (R.id.rl_praise_count)
    void onLlPraiseCountClicked() {
      PraiseActivity.start(HomeActivity.this, Account.inst().getHasNewPraise());
      mDlContent.closeDrawer(mFlRight);
    }

    @OnClick (R.id.ll_feedback)
    void onLlFeedbackClicked() {
      FeedbackActivity.start(HomeActivity.this);
      mDlContent.closeDrawer(mFlRight);
    }

    @OnClick (R.id.ll_settings)
    void onLlSettingsClicked() {
      startActivity(new Intent(HomeActivity.this, MainSettingsActivity.class));
      mDlContent.closeDrawer(mFlRight);
    }

    @OnClick (R.id.ll_help)
    void onLlHelpClicked() {
      startActivity(new Intent(HomeActivity.this, HelpActivity.class));
      mDlContent.closeDrawer(mFlRight);
    }

    @OnClick (R.id.rl_topic_list)
    void onLlTopicListClicked() {
      TopicListActivity.start(HomeActivity.this);
      mDlContent.closeDrawer(mFlRight);
    }
  }

}
