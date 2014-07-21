package com.utree.eightysix.app.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ImportContactActivity;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.StartPublishActivityEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.msg.PraiseActivity;
import com.utree.eightysix.app.publish.FeedbackActivity;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.settings.MainSettingsActivity;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.request.CircleSideRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TopBar;
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

  public static void start(Context context) {
    Intent intent = new Intent(context, FeedActivity.class);
    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);
    context.startActivity(intent);
  }

  public static void start(Context context, int id) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    context.startActivity(intent);
  }

  public static Intent getIntent(Context context, int id) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    return intent;
  }

  @OnClick (R.id.ib_send)
  public void onIbSendClicked() {
    if (!mFeedFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, mFeedFragment.getCircle().id);
    }
  }

  @OnClick (R.id.tv_more)
  public void onSideMoreClicked() {
    startActivity(new Intent(this, BaseCirclesActivity.class));
  }

  @OnItemClick (R.id.lv_side_circles)
  public void onLvSideItemClicked(int position) {
    Circle circle = mSideCircles.get(position);
    if (circle != null && !circle.selected) {
      for (Circle c : mSideCircles) {
        c.selected = false;
      }

      mFeedFragment.setCircle(circle);
      setSideHighlight(circle);
      mDlContent.closeDrawer(mLlSide);
    }
  }

  @Override
  public void onActionLeftClicked() {
    if (mDlContent.isDrawerOpen(mLlSide)) {
      mDlContent.closeDrawer(mLlSide);
    } else {
      mDlContent.openDrawer(mLlSide);
    }
  }

  @Override
  public void onActionOverflowClicked() {
    if (!mPopupMenu.isShowing()) {
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
      mPopupMenu = new PopupWindow(menu, dp2px(190), dp2px(225) + 4);
      mMenuViewHolder = new MenuViewHolder(menu);
      mPopupMenu.setFocusable(true);
      mPopupMenu.setOutsideTouchable(true);
      mPopupMenu.setBackgroundDrawable(new BitmapDrawable(getResources()));
    }

    //getTopBar().mTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
    //    getResources().getDrawable(R.drawable.top_bar_arrow_down), null);

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return null;
      }

      @Override
      public Drawable getIcon(int position) {
        if (position == 0) {
          return getResources().getDrawable(R.drawable.ic_action_msg);
        } else if (position == 1) {
          return getResources().getDrawable(R.drawable.ic_action_refresh);
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
          MsgActivity.start(FeedActivity.this, Account.inst().getNewCommentCount() > 0);
        } else if (position == 1) {
          mFeedFragment.refresh();
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

    if (U.useFixture()) {
      getTopBar().getActionView(0).setCount(99);
      getTopBar().getActionOverflow().setHasNew(true);
    }

    mFeedFragment = new FeedFragment();
    getSupportFragmentManager().beginTransaction().add(R.id.fl_feed, mFeedFragment, "feed").commitAllowingStateLoss();

    onNewIntent(getIntent());
  }

  @Override
  protected void onDestroy() {
    Env.setFirstRun(FIRST_RUN_KEY, false);
    super.onDestroy();
  }

  @Override
  protected void onPause() {
    super.onPause();
    U.getBus().unregister(mLvSideCircles);
    Env.setLastCircle(mFeedFragment.getCircle());
  }

  @Override
  protected void onResume() {
    super.onResume();
    U.getBus().register(mLvSideCircles);
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

  @Override
  public void onBackPressed() {
    if (mFeedFragment.onBackPressed()) {
      return;
    }

    moveTaskToBack(true);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    //region 标题栏数据处理
    Circle circle = intent.getParcelableExtra("circle");

    if (circle != null) {
      mFeedFragment.setCircle(circle);
    }

    if (mFeedFragment.getCircle() != null) {
      setSideHighlight(mFeedFragment.getCircle());
    }
    //endregion


    //region 侧边栏数据处理
    requestSideCircle();
    //if (mSideCircles != null) {
    //  for (Iterator<Circle> iterator = mSideCircles.iterator(); iterator.hasNext(); ) {
    //    Circle c = iterator.next();
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

    mDlContent.closeDrawer(mLlSide);

    setHasNewPraise();
    setNewCommentCount();
  }

  @Subscribe
  public void onInviteClicked(InviteClickedEvent event) {
    startActivity(new Intent(this, ImportContactActivity.class));
  }

  @Subscribe
  public void onUnlockClicked(UnlockClickedEvent event) {
    showInviteDialog();
  }

  @Subscribe
  public void onStartPublishActivity(StartPublishActivityEvent event) {
    if (!mFeedFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, mFeedFragment.getCircle().id);
    }
  }

  private void showNoPermDialog() {
    if (mNoPermDialog == null) {
      mNoPermDialog = new ThemedDialog(this);
      View view = LayoutInflater.from(this).inflate(R.layout.dialog_content_locked, null);
      NoPermViewHolder noPermViewHolder = new NoPermViewHolder(view);
      noPermViewHolder.mTvFriendCount.setText(getString(R.string.current_friend_count, mFeedFragment.getCircle().friendCount));
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

          if (mFeedFragment.getCircle() == null && mSideCircles.size() > 0) {
            mFeedFragment.setCircle(mSideCircles.get(0));
            setSideHighlight(mFeedFragment.getCircle());
          }
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

  void requestSideCircle() {
    request(new CircleSideRequest("", 1), new OnResponse<CirclesResponse>() {

      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mSideCircles = response.object.lists.size() > 10 ?
              response.object.lists.subList(0, 10) : response.object.lists;

          if (mFeedFragment.getCircle() == null && mSideCircles.size() > 0) {
            mFeedFragment.setCircle(mSideCircles.get(0));
            setSideHighlight(mFeedFragment.getCircle());
          }
          selectSideCircle(mSideCircles);

          mSideCirclesAdapter = new SideCirclesAdapter(mSideCircles);
          mLvSideCircles.setAdapter(mSideCirclesAdapter);

        }
      }
    }, CirclesResponse.class);
  }

  private void showInviteDialog() {
    if (mInviteDialog == null) {
      mInviteDialog = U.getShareManager().shareAppDialog(this, mFeedFragment.getCircle().id);
    }
    if (!mInviteDialog.isShowing()) {
      mInviteDialog.show();
    }
  }

  private void setSideHighlight(Circle circle) {
    if (mSideCircles == null) return;
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

  void setTitle(Circle circle) {
    if (circle == null) return;

    setTopTitle(circle.shortName);
    setTopSubTitle(String.format(getString(R.string.friends_info), mFeedFragment.getCurrFriends(), circle.workmateCount));
    if (circle.lock == 1) {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_lock_small), null, null, null);
      getTopBar().mSubTitle.setCompoundDrawablePadding(U.dp2px(5));
    } else {
      getTopBar().mSubTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
  }

  void setMyPraiseCount(int count) {
    mMenuViewHolder.mTvPraiseCount.setText(String.format("%d个赞", count));
  }

  @Keep
  class NoPermViewHolder {

    @InjectView (R.id.tv_friend_count)
    TextView mTvFriendCount;

    @InjectView (R.id.tv_no_perm_tip)
    TextView mTvNoPermTip;

    NoPermViewHolder(View view) {
      ButterKnife.inject(this, view);

      mTvNoPermTip.setText(getString(R.string.no_perm_to_publish,
          U.getSyncClient().getSync() == null ? 3 : U.getSyncClient().getSync().unlockFriends));
    }
  }

  @Keep
  class MenuViewHolder {

    @InjectView (R.id.tv_praise_count)
    TextView mTvPraiseCount;

    @InjectView (R.id.rb_new_praise_dot)
    RoundedButton mRbNewPraiseDot;

    MenuViewHolder(View view) {
      ButterKnife.inject(this, view);
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
      startActivity(new Intent(FeedActivity.this, FeedbackActivity.class));
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_about)
    void onLlAboutClicked() {
      showToast("TODO about");
      mPopupMenu.dismiss();
    }

    @OnClick (R.id.ll_settings)
    void onLlSettingsClicked() {
      startActivity(new Intent(FeedActivity.this, MainSettingsActivity.class));
      mPopupMenu.dismiss();
    }
  }

}