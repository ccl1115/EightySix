package com.utree.eightysix.test.contacts;

import android.content.Intent;
import android.test.ServiceTestCase;
import com.utree.eightysix.contact.ContactsSync;
import com.utree.eightysix.contact.ContactsSyncService;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;

/**
 */
public class TestContactsSyncService extends ServiceTestCase<ContactsSyncService> {

    private ContactsSync mContactsSync;

    public TestContactsSyncService() {
        super(ContactsSyncService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Log.init();
        Log.activateLogging();
        Log.setDefaultLogLevel(Constants.VERBOSE);
    }

    public void testStartService() throws Exception {
        startService(new Intent(getContext(), ContactsSyncService.class));

    }

}
