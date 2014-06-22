package com.utree.eightysix.contact;

/**
 */
public class ContactsSyncEvent {


    private boolean mSucceed;

    public ContactsSyncEvent(boolean succeed) {
        mSucceed = succeed;
    }

    public boolean isSucceed() {
        return mSucceed;
    }
}
