package com.utree.eightysix.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.contact.Contact;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import java.util.List;

/**
 */
@Layout(R.layout.activity_contacts_sync_demo)
@TopTitle(R.string.title_contacts_sync_demo_activity)
public class ContactsSyncDemoActivity extends BaseActivity {

    @ViewId(R.id.btn_cached)
    @OnClick
    public Button mBtnCached;

    @ViewId(R.id.btn_phone)
    @OnClick
    public Button mPhone;

    @ViewId(R.id.btn_sync)
    @OnClick
    public Button mSync;

    @ViewId(R.id.lv_contacts)
    public ListView mLvContacts;

    @Subscribe public void onContactsSync(ContactsSyncEvent event) {
        if (event.isSucceed()) {
            Toast.makeText(this, "sync succeed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "sync failed", Toast.LENGTH_LONG).show();
        }
        mSync.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.btn_cached:
                break;
            case R.id.btn_phone:
                break;
            case R.id.btn_sync:
                startService(new Intent(this, ContactsSyncService.class));
                mSync.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private static class ContactAdapter extends BaseAdapter {

        private List<Contact> mContacts;

        ContactAdapter(List<Contact> contacts) {
            mContacts = contacts;
        }

        @Override
        public int getCount() {
            return mContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return mContacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_contact, null);
            }

            Contact contact = mContacts.get(position);
            ((TextView) convertView.findViewById(R.id.tv_id)).setText(String.valueOf(contact.contactId));
            ((TextView) convertView.findViewById(R.id.tv_name)).setText(contact.name);
            ((TextView) convertView.findViewById(R.id.tv_phone)).setText(contact.phoneNumber);
            return convertView;
        }
    }
}