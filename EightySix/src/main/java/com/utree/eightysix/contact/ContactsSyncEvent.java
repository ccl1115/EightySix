package com.utree.eightysix.contact;

import java.util.List;

/**
 */
public class ContactsSyncEvent {

    private List<Contact> mContacts;

    public ContactsSyncEvent(List<Contact> contacts) {
        mContacts = contacts;
    }

    public List<Contact> getContacts() {
        return mContacts;
    }

}
