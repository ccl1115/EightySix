package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.friends.UserSearchActivity;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.qrcode.QRCodeScanEvent;
import com.utree.eightysix.qrcode.QRCodeScanFragment;
import com.utree.eightysix.widget.AdvancedListView;

/**
 * @author simon
 */
@TopTitle(R.string.add_new_friend)
@Layout(R.layout.activity_add_friend)
public class AddFriendActivity extends BaseActivity {

  private QRCodeScanFragment mQRCodeScanFragment;


  public class HeadViewHolder {

    @InjectView(R.id.tv_search_hint)
    public EditText mEtSearchHint;

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

    @OnClick(R.id.ll_qq)
    public void onLlQqClicked() {
      Circle currentCircle = Account.inst().getCurrentCircle();
      if (currentCircle != null) {
        U.getShareManager().shareAppToQQ(AddFriendActivity.this, currentCircle);
      } else {
        U.showToast("请先设置在职工厂");
      }
    }

    @OnClick(R.id.ll_qzone)
    public void onLlQzoneClicked() {
      Circle currentCircle = Account.inst().getCurrentCircle();
      if (currentCircle != null) {
        U.getShareManager().shareAppToQzone(AddFriendActivity.this, currentCircle);
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

      mEtSearchHint.setHint("输入蓝莓ID或者昵称");
    }
  }

  @InjectView(R.id.content)
  public AdvancedListView mAlvRecommended;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    View view = getLayoutInflater().inflate(R.layout.head_add_friend, mAlvRecommended, false);

    HeadViewHolder headViewHolder = new HeadViewHolder(view);

    mAlvRecommended.addHeaderView(view);

    mAlvRecommended.setAdapter(new BaseAdapter() {
      @Override
      public int getCount() {
        return 0;
      }

      @Override
      public Object getItem(int position) {
        return null;
      }

      @Override
      public long getItemId(int position) {
        return 0;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        return null;
      }
    });
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

    if (U.getQRCodeActionDispatcher().dispatch(event.getText())) {
      startActivity(new Intent(this, ScanFriendsActivity.class));
    }
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