package com.utree.eightysix.app.account;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.contact.Contact;
import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class ContactsAdapter extends BaseAdapter {

  private static final int TYPE_HEAD = 0;
  private static final int TYPE_CONTACT = 1;

  private List<Contact> mContacts;

  private SparseBooleanArray mChecked;
  private int mCheckedCount = 0;

  public ContactsAdapter(List<Contact> contact) {
    mContacts = contact;
    mChecked = new SparseBooleanArray();
  }

  public List<Contact> getChecked() {
    List<Contact> ret = new ArrayList<Contact>();
    for (int i = 0, size = mContacts.size(); i < size; i++) {
      if (mChecked.get(i)) {
        ret.add(mContacts.get(i));
      }
    }
    return ret;
  }

  public void add(List<Contact> contacts) {
    if (mContacts == null) {
      mContacts = contacts;
    } else {
      mContacts.addAll(contacts);
    }
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mContacts == null ? 0 : mContacts.size() + 1;
  }

  @Override
  public Contact getItem(int i) {
    return i == 0 ? null : mContacts.get(i - 1);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    switch (getItemViewType(position)) {
      case TYPE_CONTACT:
        ContactViewHolder holder;
        if (view == null) {
          view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
          holder = new ContactViewHolder(view);
          view.setTag(holder);
        } else {
          holder = (ContactViewHolder) view.getTag();
        }

        Contact contact = getItem(position);

        holder.mPosition = position;
        holder.mCbCheck.setChecked(mChecked.get(position));
        holder.mTvName.setText(contact.name);
        holder.mTvPhone.setText(contact.phone);
        break;
      case TYPE_HEAD:
        if (view == null) {
          view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_head, viewGroup, false);
          ((TextView) view.findViewById(R.id.tv_head)).setText("通讯录");
        }
        break;
    }


    return view;
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? TYPE_HEAD : TYPE_CONTACT;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  public int getCheckedCount() {
    return mCheckedCount;
  }

  public class ContactViewHolder {
    @InjectView (R.id.tv_name)
    public TextView mTvName;

    @InjectView (R.id.tv_phone)
    public TextView mTvPhone;

    @InjectView (R.id.cb_check)
    public CheckBox mCbCheck;

    public int mPosition;

    public ContactViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnCheckedChanged (R.id.cb_check)
    public void onCbCheckChecked(boolean c) {
      if (mChecked.get(mPosition) != c) {
        if (c) {
          mCheckedCount++;
        } else {
          mCheckedCount--;
        }
        U.getBus().post(new ContactsActivity.ContactCheckedCountChanged(mCheckedCount));
      }

      mChecked.put(mPosition, c);
    }
  }

}
