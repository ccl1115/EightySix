package com.utree.eightysix.app.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.data.MyFriends;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.MyFriendsRequest;
import com.utree.eightysix.response.MyFriendsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.QRCodeGenerator;
import com.utree.eightysix.widget.TopBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
@TopTitle(R.string.my_friends)
@Layout(R.layout.activity_account)
public class AccountActivity extends BaseActivity {

  @InjectView(R.id.iv_qr_code)
  public ImageView mIvQRCode;

  @InjectView(R.id.tv_id)
  public TextView mTvMyId;

  @InjectView(R.id.tv_people_count)
  public TextView mTvPeopleCount;

  @InjectView(R.id.tv_contact)
  public TextView mTvContact;

  @InjectView(R.id.tv_scan)
  public TextView mTvScan;

  @InjectView(R.id.ll_parent)
  public LinearLayout mLlParent;
  private QRCodeGenerator mQRCodeGenerator;
  private String mId;

  @OnClick(R.id.rl_id)
  public void onRlIdClicked() {
    if (mId != null) {
      MyQRCodeActivity.start(this, mId);
    }
  }

  @OnClick(R.id.tv_contact)
  public void onTvContactClicked() {
    startActivity(new Intent(this, ContactFriendsActivity.class));
  }

  @OnClick(R.id.tv_scan)
  public void onTvScanClicked() {
    startActivity(new Intent(this, ScanFriendsActivity.class));
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mQRCodeGenerator = new QRCodeGenerator();

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return "添加";
      }

      @Override
      public Drawable getIcon(int position) {
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return new RoundRectDrawable(dp2px(4), getResources().getColorStateList(R.color.apptheme_primary_btn_light));
      }

      @Override
      public void onClick(View view, int position) {
        startActivity(new Intent(AccountActivity.this, AddFriendActivity.class));
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
      }
    });


    mTvMyId.setText(getString(R.string.my_id, 0));
    mTvPeopleCount.setText(getString(R.string.people_know_you_count, 0));
    mTvContact.setText(getString(R.string.friends_in_contact, 0));
    mTvScan.setText(getString(R.string.friends_from_scan, 0));
  }

  @Override
  protected void onResume() {
    super.onResume();

    requestMyFriend();
  }

  private void requestMyFriend() {
    request(new MyFriendsRequest(), new OnResponse2<MyFriendsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(MyFriendsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mId = response.object.myViewId;
          mTvMyId.setText(getString(R.string.my_id, mId));
          mTvPeopleCount.setText(getString(R.string.people_know_you_count, response.object.friendCount));
          mTvContact.setText(getString(R.string.friends_in_contact, response.object.contactsCount));
          mTvScan.setText(getString(R.string.friends_from_scan, response.object.qrCodeFriends));

          mQRCodeGenerator.generate("eightysix://friend/add/" + response.object.myViewId, new QRCodeGenerator.OnResult() {
            @Override
            public void onResult(Bitmap bitmap) {
              mIvQRCode.setImageBitmap(bitmap);
            }
          });

          addItems(response.object);
        }
      }
    }, MyFriendsResponse.class);
  }

  private View buildItem(MyFriends.CircleFriends friend) {
    View view = LayoutInflater.from(this).inflate(R.layout.item_circle_friends, mLlParent, false);

    ((TextView) view.findViewById(R.id.tv_name)).setText(friend.name);
    ((TextView) view.findViewById(R.id.tv_count)).setText(String.valueOf(friend.friendCount));

    return view;
  }

  private List<View> mViews = new ArrayList<View>();

  private void removeItems() {
    for (View view : mViews) {
      mLlParent.removeView(view);
    }
    mViews.clear();
  }

  private void addItems(MyFriends myFriends) {
    removeItems();

    for (MyFriends.CircleFriends friends : myFriends.circleFriends) {
      View child = buildItem(friends);
      mViews.add(child);
      mLlParent.addView(child);
    }
  }
}
