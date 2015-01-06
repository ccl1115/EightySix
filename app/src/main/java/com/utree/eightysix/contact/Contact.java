package com.utree.eightysix.contact;


import java.util.Comparator;

/**
 */
public class Contact implements Comparable<Contact>, Comparator<Contact> {
    public String name;
    public String phone;

    @Override
    public String toString() {
        return "Contact{ name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }


    @Override
    public int compareTo(Contact another) {
        int ret = 0;
        if (another != null) {
            ret = (phone == null ? "" : phone).compareTo(another.phone == null ? "" : another.phone);
            if(ret == 0){
                ret = (name == null ? "" : name).compareTo(another.name == null ? "" : another.name);
            }
        } else {
            ret = 1;
        }
        return ret;
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

        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (phone != null ? !phone.equals(contact.phone) : contact.phone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }
}
