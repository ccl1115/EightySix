package com.utree.eightysix.contact;

import java.util.List;

/**
 */
public class ContactSyncEvent {

    private List<Contact> mContacts;

    public ContactSyncEvent(List<Contact> contacts) {
        mContacts = contacts;
    }

    public List<Contact> getContacts() {
        return mContacts;
    }

}
