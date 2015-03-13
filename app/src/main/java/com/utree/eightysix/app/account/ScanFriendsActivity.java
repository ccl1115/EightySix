package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.data.ScanFriends;
import com.utree.eightysix.request.ScanFriendsRequest;
import com.utree.eightysix.response.ScanFriendsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 * @author simon
 */
@Layout(R.layout.activity_scan_friends)
public class ScanFriendsActivity extends BaseActivity {

  @InjectView(R.id.lv_scan)
  public ListView mLvScan;

  private ScanFriendsAdapter mScanFriendsAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTopTitle("扫一扫添加的朋友");

    requestScanFriends();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  private void requestScanFriends() {
    showProgressBar();
    request(new ScanFriendsRequest(), new OnResponse2<ScanFriendsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(ScanFriendsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mScanFriendsAdapter = new ScanFriendsAdapter(response.object);
          mLvScan.setAdapter(mScanFriendsAdapter);
        }

        hideProgressBar();
      }
    }, ScanFriendsResponse.class);
  }

  class ScanFriendsAdapter extends BaseAdapter {

    private static final int TYPE_HEAD = 0;
    private static final int TYPE_ITEM = 1;

    ScanFriends mScanFriends;

    ScanFriendsAdapter(ScanFriends friends) {
      mScanFriends = friends;
    }

    @Override
    public int getCount() {
      return mScanFriends.friends.lists.size() + 1;
    }

    @Override
    public ScanFriends.ScanFriend getItem(int position) {
      if (position < 1) return null;
      return mScanFriends.friends.lists.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public int getItemViewType(int position) {
      if (position == 0) {
        return TYPE_HEAD;
      } else {
        return TYPE_ITEM;
      }
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      switch (getItemViewType(position)) {
        case TYPE_HEAD:
          return getHeadView(convertView, parent);
        case TYPE_ITEM:
          return getItemView(position, convertView, parent);
      }
      return null;
    }

    private View getHeadView(View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = getLayoutInflater().inflate(R.layout.item_head, parent, false);
      }

      ((TextView) convertView.findViewById(R.id.tv_head)).setText("朋友添加记录");

      return convertView;
    }

    private View getItemView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = getLayoutInflater().inflate(R.layout.item_friend, parent, false);
      }

      ScanFriends.ScanFriend friend = getItem(position);

      ((TextView) convertView.findViewById(R.id.tv_name)).setText(friend.name);
      ((TextView) convertView.findViewById(R.id.tv_info)).setText(friend.createTime);

      return convertView;
    }
  }
}