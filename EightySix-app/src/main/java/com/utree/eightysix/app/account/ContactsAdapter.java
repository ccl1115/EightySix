package com.utree.eightysix.app.account;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import com.utree.eightysix.R;
import com.utree.eightysix.contact.Contact;
import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class ContactsAdapter extends BaseAdapter {

  private List<Contact> mContacts;

  private SparseBooleanArray mChecked;

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
    return mContacts == null ? 0 : mContacts.size();
  }

  @Override
  public Contact getItem(int i) {
    return mContacts.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {

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
    holder.mTvName.setText(contact.name);
    holder.mTvPhone.setText(contact.phone);

    return view;
  }

  public class ContactViewHolder {
    @InjectView (R.id.tv_name)
    public TextView mTvName;

    @InjectView (R.id.tv_phone)
    public TextView mTvPhone;

    @OnCheckedChanged(R.id.cb_check)
    public void onCbCheckChecked(boolean c) {
      mChecked.put(mPosition, c);
    }

    public int mPosition;

    public ContactViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}
