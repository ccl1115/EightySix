package com.utree.eightysix.app.feed;

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
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.*;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.SyncClient;
import com.utree.eightysix.app.account.AccountActivity;
import com.utree.eightysix.app.account.AddFriendActivity;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.feed.event.*;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.msg.PraiseActivity;
import com.utree.eightysix.app.publish.FeedbackActivity;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.settings.HelpActivity;
import com.utree.eightysix.app.settings.MainSettingsActivity;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.request.CircleSideRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TopBar;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 */
@Layout (R.layout.activity_feed)
public class FeedActivity extends BaseActivity {

  private static final String FIRST_RUN_KEY = "feed";

  @InjectView (R.id.lv_side_circles)
  public AdvancedListView mLvSideCircles;

  @InjectView (R.id.ib_send)
  public ImageButton mSend;

  @InjectView (R.id.ll_side)
  public LinearLayout mLlSide;

  @InjectView (R.id.content)
  public DrawerLayout mDlContent;

  private SideCirclesAdapter mSideCirclesAdapter;

  private PopupWindow mPopupMenu;

  private boolean mSideShown;

  private List<Circle> mSideCircles;

  private FeedFragment mFeedFragment;
  private RewardFragment mRewardFragment;

  /**
   * 邀请好友对话框
   */
  private ThemedDialog mInviteDialog;

  /**
   * 没有发帖权限对话框
   */
  private ThemedDialog mNoPermDialog;

  private boolean mRefreshed;
  private MenuViewHolder mMenuViewHolder;
  private ThemedDialog mUnlockDialog;

  private boolean mShouldExit;

  public static void start(Context context) {
    Intent intent = new Intent(context, FeedActivity.class);
    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);
    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle, boolean skipCache) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);
    intent.putExtra("skipCache", skipCache);
    context.startActivity(intent);
  }

  public static void start(Context context, int id) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    context.startActivity(intent);
  }

  public static void start(Context context, int id, boolean skipCache) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    intent.putExtra("skipCache", skipCache);
    context.startActivity(intent);
  }

  public static Intent getIntent(Context context, int id, boolean skipCache) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    intent.putExtra("skipCache", skipCache);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    intent.setAction(String.valueOf(id));
    return intent;
  }

  @OnClick (R.id.ib_send)
  public void onIbSendClicked() {
    U.getAnalyser().trackEvent(this, "feed_publish", "feed_publish");
    if (!mFeedFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, mFeedFragment.getCircleId());
    }
  }

  @OnClick (R.id.tv_more)
  public void onSideMoreClicked() {
    U.getAnalyser().trackEvent(this, "side_more", "side_more");
    startActivity(new Intent(this, BaseCirclesActivity.class));
  }

  @OnItemClick (R.id.lv_side_circles)
  public void onLvSideItemClicked(int position) {
    U.getAnalyser().trackEvent(this, "side_switch", "side_switch");
    Circle circle = mSideCircles.get(position);
    if (circle != null) {
      for (Circle c : mSideCircles) {
        c.selected = false;
      }

      mFeedFragment.setCircle(circle, true);
      setSideHighlight(circle);
      mDlContent.closeDrawer(mLlSide);
      mFeedFragment.mRefresherView.setRefreshing(true);
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          requestSideCircle();
        }
      }, 1000);
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
    if (mSideCirclesAdapter == null || mSideCirclesAdapter.getCount() == 0) {
      requestSideCircle();
      showProgressBar();
    }
  }

  @Override
  public void onActionOverflowClicked() {
    openMenu();
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

  @Override
  public boolean showActionOverflow() {
    return true;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ContactsSyncService.start(this, false);

    setActionLeftDrawable(getResources().getDrawable(R.drawable.ic_drawer));

    if (mPopupMenu == null) {
      LinearLayout menu = (LinearLayout) View.inflate(FeedActivity.this, R.layout.widget_feed_menu, null);
      mPopupMenu = new PopupWindow(menu, dp2px(190), dp2px(270) + 5);
      mMenuViewHolder = new MenuViewHolder(menu);
      mPopupMenu.setFocusable(true);
      mPopupMenu.setIgnoreCheekPress();
      mPopupMenu.setBackgroundDrawable(new BitmapDrawable(getResources()));
    }

    setActionAdapter();

    mFeedFragment = new FeedFragment();
    getSupportFragmentManager().beginTransaction().add(R.id.fl_feed, mFeedFragment, "feed").commitAllowingStateLoss();

    onNewIntent(getIntent());
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
        } else {
          Sync sync = U.getSyncClient().getSync();
          if (position == 1 || sync == null || sync.activeSys == 1) {
            return getResources().getDrawable(R.drawable.ic_action_reward);
          }
        }
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        if (position == 0) {
          return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
        } else {
          Sync sync = U.getSyncClient().getSync();
          if (position == 1 || sync == null || sync.activeSys == 1) {
            return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
          }
        }
        return null;
      }

      @Override
      public void onClick(View view, int position) {
        if (position == 0) {
          U.getAnalyser().trackEvent(FeedActivity.this, "feed_msg", "feed_msg");
          MsgActivity.start(FeedActivity.this, Account.inst().getNewCommentCount() > 0);
        } else {
          Sync sync = U.getSyncClient().getSync();
          if (position == 1 || sync == null || sync.activeSys == 1) {
            if (mRewardFragment == null) {
              mRewardFragment = new RewardFragment();
              getSupportFragmentManager().beginTransaction()
                  .add(android.R.id.content, mRewardFragment)
                  .commit();
            } else if (mRewardFragment.isDetached()) {
              getSupportFragmentManager().beginTransaction().attach(mRewardFragment).commit();
            }
          }
        }
      }

      @Override
      public int getCount() {
        Sync sync = U.getSyncClient().getSync();
        return 1 + ((sync == null || sync.activeSys == 1) ? 1 : 0);
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }
    });
  }

  @Override
  protected void onDestroy() {
    Env.setFirstRun(FIRST_RUN_KEY, false);
    super.onDestroy();
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

  @Override
  protected void onPause() {
    super.onPause();
    M.getRegisterHelper().unregister(mLvSideCircles);
    Env.setLastCircle(mFeedFragment.getCircle());
  }

  @Override
  protected void onResume() {
    super.onResume();
    M.getRegisterHelper().register(mLvSideCircles);

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
    if (mRewardFragment != null && mRewardFragment.onBackPressed()){
      return;
    }

    if (mFeedFragment != null && mFeedFragment.onBackPressed()) {
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
  protected void onNewIntent(Intent intent) {

    //region 标题栏数据处理
    Circle circle = intent.getParcelableExtra("circle");

    boolean skipCache = intent.getBooleanExtra("skipCache", false);

    if (circle != null) {
      mFeedFragment.setCircle(circle, skipCache);
    } else {
      final int circleId = intent.getIntExtra("id", 0);
      mFeedFragment.setCircle(circleId, skipCache);
    }

    if (mFeedFragment.getCircle() != null) {
      setSideHighlight(mFeedFragment.getCircle());
    }
    //endregion


    //region 侧边栏数据处理
    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        requestSideCircle();
      }
    }, 500);
    //if (mSideCircles != null) {
    //  for (Iterator<Circle> iterator = mSideCircles.iterator(); iterator.hasNext(); ) {
    //    Circle c = iterator.next()
    // ;
    //    if (c == null) iterator.remove();
    //  }
    //  selectSideCircle(mSideCircles);
    //  mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
    //  mLvSideCircles.setAdapter(mSideCirclesAdapter);
    //} else if (U.useFixture()) {
    //  mSideCircles = U.getFixture(Circle.class, 10, "valid");
    //  selectSideCircle(mSideCircles);
    //  mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
    //  mLvSideCircles.setAdapter(mSideCirclesAdapter);
    //} else {
    //  cacheOutSideCircle();
    //}
    //endregion

    setHasNewPraise();
    setNewCommentCount();
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
    if (!mFeedFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, mFeedFragment.getCircleId());
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
      spannableString.setSpan(span, index, index +4, 0);
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

  private void selectSideCircle(List<Circle> sideCircles) {
    if (sideCircles != null) {
      for (Circle c : sideCircles) {
        c.selected = false;
      }

      if (mFeedFragment.getCircle() == null) return;

      for (Circle c : sideCircles) {
        if (mFeedFragment.getCircle().name.equals(c.name)) {
          c.selected = true;
          break;
        }
      }
    }
  }

  private void cacheOutSideCircle() {
    cacheOut(new CircleSideRequest("", 1), new OnResponse<CirclesResponse>() {
      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mSideCircles = response.object.lists.size() > 10 ?
              response.object.lists.subList(0, 10) : response.object.lists;

          setSideHighlight(mFeedFragment.getCircle());
          selectSideCircle(mSideCircles);

          mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
          mLvSideCircles.setAdapter(mSideCirclesAdapter);

        } else {
          requestSideCircle();
        }
      }
    }, CirclesResponse.class);

    showProgressBar();
  }

  private void showInviteDialog() {
    if (mInviteDialog == null) {
      mInviteDialog = U.getShareManager().shareAppDialog(this, mFeedFragment.getCircle());
    }
    if (!mInviteDialog.isShowing()) {
      mInviteDialog.show();
    }
  }

  private void setSideHighlight(Circle circle) {
    if (mSideCircles == null || circle == null) return;
    for (Circle c : mSideCircles) {
      c.selected = circle.equals(c);
    }
    setTitle(circle);
    if (mSideCirclesAdapter != null) mSideCirclesAdapter.notifyDataSetChanged();
  }

  private void setHasNewPraise() {
    mMenuViewHolder.mRbNewPraiseDot.setVisibility(Account.inst().getHasNewPraise() ? View.VISIBLE : View.INVISIBLE);
    getTopBar().getActionOverflow().setHasNew(Account.inst().getHasNewPraise());
  }

  private void setNewCommentCount() {
    getTopBar().getActionView(0).setCount(Account.inst().getNewCommentCount());
  }

  void requestSideCircle() {
    request(new CircleSideRequest("", 1), new OnResponse2<CirclesResponse>() {

      @Override
      public void onResponseError(Throwable e) {
      }

      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mSideCircles = response.object.lists.size() > 10 ?
              response.object.lists.subList(0, 10) : response.object.lists;

          setSideHighlight(mFeedFragment.getCircle());
          selectSideCircle(mSideCircles);

          mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
          mLvSideCircles.setAdapter(mSideCirclesAdapter);

        }
        hideProgressBar();
      }


    }, CirclesResponse.class);
  }

  void setTitle(Circle circle) {
    if (circle == null) return;

    setTopTitle(circle.shortName);
//    setTopSubTitle(String.format(getString(R.string.friends_info),
//        mFeedFragment.getCurrFriends(), mFeedFragment.getWorkerCount()));
    if (circle.lock == 1) {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_lock_small), null, null, null);
      getTopBar().mSubTitle.setCompoundDrawablePadding(U.dp2px(5));
    } else {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
  }

//  void setMyPraiseCount(int count, String praisePercent, int variant) {
//    if (count == 0) {
//      mMenuViewHolder.mTvPraiseCount.setText("我收到的赞");
//    } else {
//      mMenuViewHolder.mTvPraiseCount.setText(String.format("收到%d个赞", count));
//    }
//  }

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

    @InjectView(R.id.rb_settings_dot)
    RoundedButton mRbSettingsDot;

    MenuViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.ll_my_friends)
    void onLlMyFriendsClicked() {
      startActivity(new Intent(FeedActivity.this, AccountActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick(R.id.rb_add)
    void onRbAddClicked() {
      startActivity(new Intent(FeedActivity.this, AddFriendActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_invite)
    void onLlInviteClicked() {
      showInviteDialog();
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_praise_count)
    void onLlPraiseCountClicked() {
      PraiseActivity.start(FeedActivity.this, Account.inst().getHasNewPraise());
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_feedback)
    void onLlFeedbackClicked() {
      FeedbackActivity.start(FeedActivity.this);
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_settings)
    void onLlSettingsClicked() {
      startActivity(new Intent(FeedActivity.this, MainSettingsActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick(R.id.ll_help)
    void onLlHelpClicked() {
      startActivity(new Intent(FeedActivity.this, HelpActivity.class));
      mPopupMenu.dismiss();
    }
  }

}
