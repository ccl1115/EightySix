package com.utree.eightysix.contact;

import java.util.List;

/**
 * Fire when read out all contacts in phone
 *
 * @author simon
 */
public class ContactsReadEvent {
  private List<Contact> mContacts;

  public ContactsReadEvent(List<Contact> contacts) {
    mContacts = contacts;
  }

  public List<Contact> getContacts() {
    return mContacts;
  }


}
