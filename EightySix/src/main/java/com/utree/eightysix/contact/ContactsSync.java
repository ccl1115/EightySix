package com.utree.eightysix.contact;

import android.content.Context;
import android.content.Intent;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import java.util.List;

/**
 */
public class ContactsSync {

    private Context mContext;

    private List<Contact> mContacts;

    public ContactsSync(Context context) {
        mContext = context;
        U.getBus().register(this);
    }

    public List<Contact> getContacts() {
        if (mContacts == null) {
            mContext.startService(new Intent(mContext, ContactsSyncService.class));
        }
        return mContacts;
    }

    @Subscribe public void onContactsSync(ContactsSyncEvent event) {
        mContacts = event.getContacts();
    }
}
