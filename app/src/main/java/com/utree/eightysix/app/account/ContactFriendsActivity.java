package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.data.ContactFriends;
import com.utree.eightysix.request.ContactFriendsRequest;
import com.utree.eightysix.response.ContactFriendsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 * @author simon
 */
@Layout(R.layout.activity_contact_friends)
public class ContactFriendsActivity extends BaseActivity {

  @InjectView(R.id.alv_contacts)
  public ListView mAlvContacts;

  private ContactFriendAdapter mContactFriendAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTopTitle("通讯录联系人");

    requestContactFriends();
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

  private void requestContactFriends() {
    showProgressBar();
    request(new ContactFriendsRequest(), new OnResponse2<ContactFriendsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(ContactFriendsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mContactFriendAdapter = new ContactFriendAdapter(response.object);
          mAlvContacts.setAdapter(mContactFriendAdapter);
        }
        hideProgressBar();
      }
    }, ContactFriendsResponse.class);
  }

  class ContactFriendAdapter extends BaseAdapter {

    private static final int TYPE_UNREG_COUNT = 0;
    private static final int TYPE_HEAD = 1;
    private static final int TYPE_ITEM = 2;

    private ContactFriends mContactFriends;

    ContactFriendAdapter(ContactFriends friends) {
      mContactFriends = friends;
    }

    @Override
    public int getCount() {
      return mContactFriends.friends.lists.size() + 2;
    }

    @Override
    public ContactFriends.Friend getItem(int position) {
      if (position < 2) return null;
      return mContactFriends.friends.lists.get(position - 2);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public int getItemViewType(int position) {
      if (position == 0) {
        return TYPE_UNREG_COUNT;
      } else if (position == 1) {
        return TYPE_HEAD;
      } else {
        return TYPE_ITEM;
      }
    }

    @Override
    public int getViewTypeCount() {
      return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      switch (getItemViewType(position)) {
        case TYPE_ITEM:
          return getItemView(position, convertView, parent);
        case TYPE_HEAD:
          return getHeadView(convertView, parent);
        case TYPE_UNREG_COUNT:
          return getUnregView(convertView, parent);
      }
      return null;
    }

    private View getItemView(int position, View convertView, ViewGroup parent) {
      ItemViewHolder holder;
      if (convertView == null) {
        convertView = LayoutInflater.from(ContactFriendsActivity.this)
            .inflate(R.layout.item_friend, parent, false);
        holder = new ItemViewHolder(convertView);
        convertView.setTag(holder);
      } else {
        holder = (ItemViewHolder) convertView.getTag();
      }

      ContactFriends.Friend friend = getItem(position);

      holder.mTvInfo.setText(friend.viewId);
      holder.mTvName.setText(friend.name);

      return convertView;
    }

    private View getHeadView(View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(ContactFriendsActivity.this)
            .inflate(R.layout.item_head, parent, false);

        ((TextView) convertView.findViewById(R.id.tv_head)).setText("我的朋友");
      }
      return convertView;
    }

    private View getUnregView(View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(ContactFriendsActivity.this)
            .inflate(R.layout.item_unreg, parent, false);

        TextView view = (TextView) convertView.findViewById(R.id.tv_unreg);
        view.setText(String.format("短信邀请通讯录好友（%d人未加入）", mContactFriends.unRegCount));
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            startActivity(new Intent(ContactFriendsActivity.this, ContactsActivity.class));
          }
        });
      }
      return convertView;
    }

    class ItemViewHolder {
      @InjectView(R.id.tv_name)
      TextView mTvName;

      @InjectView(R.id.tv_info)
      TextView mTvInfo;

      ItemViewHolder(View view) {
        ButterKnife.inject(this, view);
      }
    }
  }
}