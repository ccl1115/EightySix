package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.StartPublishActivityEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.app.feed.event.UploadClickedEvent;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.request.FriendsSizeRequest;
import com.utree.eightysix.response.FriendsSizeResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.ThemedDialog;

/**
 */
@Layout(R.layout.activity_feed)
public class FeedActivity extends BaseActivity {

  private static final String FIRST_RUN_KEY = "feed";

  @InjectView(R.id.ib_send)
  public ImageButton mSend;

  private TabFragment mTabFragment;

  /**
   * 邀请好友对话框
   */
  private ThemedDialog mInviteDialog;

  /**
   * 没有发帖权限对话框
   */
  private ThemedDialog mNoPermDialog;

  private ThemedDialog mUnlockDialog;

  public static void start(Context context) {
    Intent intent = new Intent(context, FeedActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, Circle circle, boolean skipCache) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("circle", circle);
    intent.putExtra("skipCache", skipCache);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, int id) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, int id, boolean skipCache) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    intent.putExtra("skipCache", skipCache);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static Intent getIntent(Context context, int id, boolean skipCache, int tabIndex) {
    Intent intent = new Intent(context, FeedActivity.class);
    intent.putExtra("id", id);
    intent.putExtra("tabIndex", tabIndex);
    intent.putExtra("skipCache", skipCache);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    intent.setAction(String.valueOf(id));
    return intent;
  }

  @OnClick(R.id.ib_send)
  public void onIbSendClicked() {
    U.getAnalyser().trackEvent(this, "feed_publish", "feed_publish");
    if (!mTabFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, mTabFragment.getCircleId(), null);
    }
  }

  @Override
  public void onActionLeftClicked() {
    U.getAnalyser().trackEvent(this, "feed_title", "feed_title");
    finish();
  }

  @Override
  public boolean showActionOverflow() {
    return false;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_action_overflow));

    Circle circle = getIntent().getParcelableExtra("circle");
    int id = getIntent().getIntExtra("id", -1);

    if (circle != null) {
      requestFriendSize(circle.id);
    } else if (id != -1) {
      requestFriendSize(id);
    } else {
      finish();
      return;
    }

    onNewIntent(getIntent());
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mResumed && getCount() > U.getConfigInt("activity.background.refresh.time")) {
      if (mTabFragment != null) {
        mTabFragment.setCircle(mTabFragment.getCircle());
      }
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onBackPressed() {
    if (mTabFragment != null && mTabFragment.onBackPressed()) {
      return;
    }

    finish();
  }

  @Override
  protected void onNewIntent(Intent intent) {
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
    if (!mTabFragment.canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(this, mTabFragment.getCircleId(), null);
    }
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
      mInviteDialog = U.getShareManager().shareAppDialog(this, mTabFragment.getCircle());
    }
    if (!mInviteDialog.isShowing()) {
      mInviteDialog.show();
    }
  }

  private void setHasNewPraise() {
  }

  private void setNewCommentCount() {
  }

  void setTitle(Circle circle) {
    if (circle == null) return;

    setTopTitle(circle.shortName);
    if (circle.lock == 1) {
      mTopBar.mSubTitle.setCompoundDrawablesWithIntrinsicBounds(
          getResources().getDrawable(R.drawable.ic_lock_small), null, null, null);
      mTopBar.mSubTitle.setCompoundDrawablePadding(U.dp2px(5));
    } else {
      mTopBar.mSubTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
  }

  @Keep
  class NoPermViewHolder {

    @InjectView(R.id.tv_no_perm_tip)
    TextView mTvNoPermTip;

    NoPermViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  private void requestFriendSize(int factoryId) {
    showProgressBar();
    request(new FriendsSizeRequest(factoryId), new OnResponse2<FriendsSizeResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mTabFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt("tabIndex", getIntent().getIntExtra("tabIndex", 0));
        args.putInt("mode", TabFragment.MODE_HAS_FRIENDS);
        mTabFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_feed, mTabFragment, "tab").commit();

        Circle circle = getIntent().getParcelableExtra("circle");
        int id = getIntent().getIntExtra("id", -1);

        if (circle != null) {
          mTabFragment.setCircle(circle);
        } else if (id != -1) {
          mTabFragment.setCircle(id);
        }
        hideProgressBar();
      }

      @Override
      public void onResponse(FriendsSizeResponse response) {
        mTabFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt("tabIndex", getIntent().getIntExtra("tabIndex", 0));
        if (response.object.friendsSize > 0) {
          args.putInt("mode", TabFragment.MODE_HAS_FRIENDS);
        } else {
          args.putInt("mode", TabFragment.MODE_MORE);
        }
        Circle circle = getIntent().getParcelableExtra("circle");
        int id = getIntent().getIntExtra("id", -1);

        if (circle != null) {
          args.putParcelable("circle", circle);
        } else if (id != -1) {
          args.putInt("id", id);
        }

        mTabFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_feed, mTabFragment, "tab").commit();

        hideProgressBar();
      }
    }, FriendsSizeResponse.class);
  }
}
