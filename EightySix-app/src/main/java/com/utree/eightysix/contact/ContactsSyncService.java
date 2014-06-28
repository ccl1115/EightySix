package com.utree.eightysix.contact;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import static android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
import static android.provider.ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER;
import static android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER;
import static android.provider.ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import com.jakewharton.disklrucache.DiskLruCache;
import com.utree.eightysix.U;
import com.utree.eightysix.request.ImportContactsRequest;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.InputValidator;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The workflow of syncing contacts:
 * <p/>
 * 0. fetch phone contacts
 * 1. if cache is null, then update cache and upload to server.
 * 2. if cache is not null, then compare phone contacts with cache.
 * 3. if equal, done nothing.
 * 4. if not equal, update cache and upload to server.
 */
public class ContactsSyncService extends IntentService {

  private Handler mHandler;

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   */
  public ContactsSyncService() {
    super("ContactsSyncService");
  }

  @Override
  public void onCreate() {
    super.onCreate();

    mHandler = new Handler();
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    final List<Contact> cache = getContactsFromCache();
    final List<Contact> phone = getContactsFromPhone();

    if (phone == null) {
      U.getBus().post(new ContactsSyncEvent(false));
      return;
    }

    if (cache == null || !compareCacheAndPhone(cache, phone)) {
      cacheContacts(phone);
      uploadContact(phone);
    }
  }

  private void uploadContact(final List<Contact> contacts) {
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        final ImportContactsRequest request = new ImportContactsRequest();
        RequestData data = U.getRESTRequester().convert(request);
        for (int i = 0; i < contacts.size(); i++) {
          Contact contact = contacts.get(i);
          data.getParams().add(String.format("contact[%d].name", i), contact.name);
          data.getParams().add(String.format("contact[%d].phone", i), contact.phone);
        }

        U.getRESTRequester().request(data, new HandlerWrapper<Response>(data, new OnResponse<Response>() {
          @Override
          public void onResponse(Response response) {
            if (response != null && response.code == 0) {
              U.getBus().post(new ContactsSyncEvent(true));
            } else {
              U.getBus().post(new ContactsSyncEvent(false));
            }
          }
        }, Response.class));
      }
    });
  }

  private List<Contact> getContactsFromCache() {
    List<Contact> contacts = new ArrayList<Contact>();
    DiskLruCache.Snapshot snapshot = null;
    try {
      snapshot = U.getContactsCache().get("contacts");
      if (snapshot == null) {
        return null;
      }
      contacts = U.getGson().fromJson(new InputStreamReader(snapshot.getInputStream(0)),
          new TypeToken<ArrayList<Contact>>() {
          }.getType()
      );
    } catch (IOException e) {
      U.getAnalyser().reportException(this, e);
    } finally {
      if (snapshot != null) snapshot.close();
    }
    Collections.sort(contacts);
    return contacts;
  }

  private boolean compareCacheAndPhone(List<Contact> cache, List<Contact> phone) {
    if (cache.size() != phone.size()) return false;

    for (int i = 0, size = cache.size(); i < size; i++) {
      if (!cache.get(i).equals(phone.get(i))) {
        return false;
      }
    }
    return true;
  }

  private void cacheContacts(List<Contact> phone) {
    OutputStream stream = null;
    OutputStreamWriter out = null;
    JsonWriter writer = null;
    try {
      DiskLruCache.Editor editor = U.getContactsCache().edit("contacts");
      stream = editor.newOutputStream(0);
      out = new OutputStreamWriter(stream);
      writer = new JsonWriter(out);
      U.getGson().toJson(phone, new TypeToken<ArrayList<Contact>>() {
      }.getType(), writer);
      editor.commit();
    } catch (IOException e) {
      U.getAnalyser().reportException(this, new Exception("Failed to invalidate cache", e));
    } finally {
      closeQuietly(writer);
      closeQuietly(out);
      closeQuietly(stream);
    }
  }

  private void closeQuietly(Closeable closable) {
    if (closable != null) {
      try {
        closable.close();
      } catch (IOException ignored) {
      }
    }
  }

  private List<Contact> getContactsFromPhone() {
    final List<Contact> contacts = new ArrayList<Contact>();

    String[] projections = {RAW_CONTACT_ID, DISPLAY_NAME, NUMBER};

    String selection = String.format("%s=1", HAS_PHONE_NUMBER);

    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projections, selection, null, null);

    if (cursor == null) return null;
    if (!cursor.moveToFirst()) return contacts;

    do {
      Contact contact = new Contact();

      contact.contactId = cursor.getInt(cursor.getColumnIndex(RAW_CONTACT_ID));
      contact.name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
      String number = cursor.getString(cursor.getColumnIndex(NUMBER));
      StringBuilder b = new StringBuilder();
      for (int i = 0, size = number.length(); i < size; i++) {
        char c = number.charAt(i);
        if (Character.isDigit(c)) {
          b.append(c);
        }
      }

      number = b.toString();

      if (number.startsWith("86")) {
        number = number.substring(2);
      }

      if (!InputValidator.phoneNumber(number)) {
        continue;
      }

      contact.phone = number;
      contacts.add(contact);
    } while (cursor.moveToNext());

    cursor.close();
    Collections.sort(contacts);

    return contacts;
  }


}
