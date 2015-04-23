package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.annotations.SerializedName;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.friends.FriendContactListActivity;
import com.utree.eightysix.app.friends.SendRequestActivity;
import com.utree.eightysix.app.friends.UserSearchActivity;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.qrcode.QRCodeScanEvent;
import com.utree.eightysix.qrcode.QRCodeScanFragment;
import com.utree.eightysix.response.FriendListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
@TopTitle(R.string.add_new_friend)
@Layout(R.layout.activity_add_friend)
public class AddFriendActivity extends BaseActivity {

  private QRCodeScanFragment mQRCodeScanFragment;
  private FriendRecommendAdapter mFriendRecommendAdapter;

  @Keep
  public static class GetInviteCodeResponse extends Response {


    @SerializedName("object")
    public HomeActivity.GetInviteCode object;
  }


  public class HeadViewHolder {

    @InjectView(R.id.tv_search_hint)
    public EditText mEtSearchHint;

    @InjectView(R.id.tv_id)
    public TextView mTvId;

    @InjectView(R.id.tv_head)
    public TextView mTvHead;

    @OnClick(R.id.ll_scan)
    public void onLlScanClicked() {
      if (mQRCodeScanFragment == null) {
        mQRCodeScanFragment = new QRCodeScanFragment();
        getSupportFragmentManager().beginTransaction()
            .add(android.R.id.content, mQRCodeScanFragment)
            .commit();
      } else if (mQRCodeScanFragment.isDetached()) {
        getSupportFragmentManager().beginTransaction()
            .attach(mQRCodeScanFragment)
            .commit();
      }
    }

    @OnClick(R.id.ll_upload_contacts)
    public void onLlUploadContacts() {
      ContactsSyncService.start(AddFriendActivity.this, true);
      showProgressBar(true);
    }

    @OnClick(R.id.ll_invite_code)
    public void onLlQqClicked() {
      showProgressBar(true);

      U.request("get_invite_code", new OnResponse2<GetInviteCodeResponse>() {
        @Override
        public void onResponseError(Throwable e) {
          hideProgressBar();
        }

        @Override
        public void onResponse(GetInviteCodeResponse response) {
          hideProgressBar();
          final ThemedDialog dialog = new ThemedDialog(AddFriendActivity.this);
          dialog.setTitle("你的专属邀请码");

          TextView textView = new TextView(AddFriendActivity.this);
          SpannableStringBuilder builder = new SpannableStringBuilder();
          builder.append(response.object.msg).append("\n\n").append("你的专属邀请码是：\n");
          SpannableString color = new SpannableString(response.object.inviteCode);
          color.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.apptheme_primary_light_color)), 0, color.length(), 0);
          builder.append(color);
          builder.append("\n\n").append("你已经邀请了").append(String.valueOf(response.object.newCount)).append("个人\n");

          textView.setText(builder);
          textView.setGravity(Gravity.CENTER);
          textView.setEms(12);
          textView.setPadding(dp2px(16), dp2px(8), dp2px(16), dp2px(8));
          textView.setTextSize(16);
          dialog.setContent(textView);
          dialog.setPositive("知道啦", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dialog.dismiss();
            }
          });

          dialog.show();
        }
      }, GetInviteCodeResponse.class, null, null);
    }

    @OnClick(R.id.ll_invite)
    public void onLlInviteClicked() {
      Circle currentCircle = Account.inst().getCurrentCircle();
      if (currentCircle != null) {
        U.getShareManager().shareAppDialog(AddFriendActivity.this, currentCircle).show();
      } else {
        U.showToast("请先设置在职工厂");
      }
    }

    @OnClick(R.id.tv_search_hint)
    public void onEtSearchHintClicked() {
      startActivity(new Intent(AddFriendActivity.this, UserSearchActivity.class));
    }

    HeadViewHolder(View view) {
      ButterKnife.inject(this, view);

      mEtSearchHint.setFocusable(false);
      mEtSearchHint.setHint("输入蓝莓ID或者昵称");
      mTvHead.setText("朋友推荐");
    }
  }

  @InjectView(R.id.content)
  public AdvancedListView mAlvRecommended;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    View view = getLayoutInflater().inflate(R.layout.head_add_friend, mAlvRecommended, false);

    final HeadViewHolder headViewHolder = new HeadViewHolder(view);

    mAlvRecommended.addHeaderView(view);

    U.request("user_friend_recommend", new OnResponse2<FriendListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final FriendListResponse response) {
        if (RESTRequester.responseOk(response)) {
          mFriendRecommendAdapter = new FriendRecommendAdapter(response.object);
          mAlvRecommended.setAdapter(mFriendRecommendAdapter);
          headViewHolder.mTvId.setText("我的蓝莓ID：" + response.extra.viewId);
          headViewHolder.mTvId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              MyQRCodeActivity.start(AddFriendActivity.this, response.extra.viewId);
            }
          });
        }
      }
    }, FriendListResponse.class);
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Subscribe
  public void onContactsSyncService(ContactsSyncEvent event) {
    if (event.isSucceed()) {
      showToast("更新通讯录成功");
    } else {
      showToast("更新通讯录失败");
    }
    startActivity(new Intent(this, FriendContactListActivity.class));
    hideProgressBar();
  }

  @Subscribe
  public void onQRCodeScanEvent(QRCodeScanEvent event) {
    if (mQRCodeScanFragment != null) {
      if (mQRCodeScanFragment.isAdded()) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
            .detach(mQRCodeScanFragment)
            .commit();
      }
    }

    if (BuildConfig.DEBUG) {
      showToast("scanned: " + event.getText());
    }

    U.getQRCodeActionDispatcher().dispatch(event.getText());
  }

  @Subscribe
  public void onSentRequestEvent(SendRequestActivity.SentRequestEvent event) {
    mFriendRecommendAdapter.setSentRequest(event.getViewId());
  }

  @Override
  public void onBackPressed() {
    if (mQRCodeScanFragment != null) {
      if (mQRCodeScanFragment.isAdded()) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
            .detach(mQRCodeScanFragment)
            .commit();
        return;
      }
    }

    super.onBackPressed();
  }
}