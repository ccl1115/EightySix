package com.utree.eightysix.contact;


import java.util.Comparator;

/**
 */
public class Contact implements Comparable<Contact>, Comparator<Contact> {
    public transient int contactId;
    public String name;
    public String phoneNumber;

    @Override
    public String toString() {
        return "Contact{" +
                "contactId=" + contactId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }


    @Override
    public int compareTo(Contact another) {
        if (another != null) {
            if (contactId > another.contactId) {
                return 1;
            } else if (contactId < another.contactId) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int compare(Contact lhs, Contact rhs) {
        return lhs.compareTo(rhs);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (contactId != contact.contactId) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(contact.phoneNumber) : contact.phoneNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contactId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        return result;
    }
}
