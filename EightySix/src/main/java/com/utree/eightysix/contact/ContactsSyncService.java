package com.utree.eightysix.contact;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import static android.provider.ContactsContract.CommonDataKinds.Phone.*;
import com.utree.eightysix.U;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

/**
 */
public class ContactsSyncService extends IntentService {

    private Handler mHandler;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ContactsSyncService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final List<Contact> contacts = new ArrayList<Contact>();

        String[] projections = {
                CONTACT_ID,
                DISPLAY_NAME,
                NUMBER,
        };

        String selection = String.format("%s=1", HAS_PHONE_NUMBER);

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                projections, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();

                    contact.contactId = cursor.getInt(cursor.getColumnIndex(CONTACT_ID));
                    contact.name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    contact.phoneNumber = cursor.getString(cursor.getColumnIndex(NUMBER));

                    contacts.add(contact);
                } while (cursor.moveToNext());
            }
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                U.getBus().post(new ContactSyncEvent(contacts));
            }
        });
    }


}
