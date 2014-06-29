package com.utree.eightysix.app.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.contact.Contact;
import java.util.List;

/**
 * @author simon
 */
public class ContactsAdapter extends BaseAdapter {

  private List<Contact> mContacts;

  public ContactsAdapter(List<Contact> contact) {
    mContacts = contact;
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
  public View getView(int i, View view, ViewGroup viewGroup) {

    ContactViewHolder holder;
    if (view == null) {
      view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
      holder = new ContactViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ContactViewHolder) view.getTag();
    }

    Contact contact = getItem(i);

    holder.mTvName.setText(contact.name);
    holder.mTvPhone.setText(contact.phone);

    return view;
  }

  public static class ContactViewHolder {
    @InjectView (R.id.tv_name)
    public TextView mTvName;

    @InjectView (R.id.tv_phone)
    public TextView mTvPhone;

    @InjectView (R.id.cb_check)
    public CheckBox mCbCheck;

    public ContactViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}
