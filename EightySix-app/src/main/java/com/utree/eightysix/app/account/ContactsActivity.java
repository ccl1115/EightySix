package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.contact.ContactsReadEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.widget.AdvancedListView;

/**
 * @author simon
 */
@Layout (R.layout.activity_contacts)
public class ContactsActivity extends BaseActivity {



  @InjectView (R.id.alv_contacts)
  public AdvancedListView mAlvContacts;
  private ContactsAdapter mContactsAdapter;

  @OnItemClick (R.id.alv_contacts)
  public void onAlvContactsItemClicked(int position) {

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    showProgressBar();
    startService(new Intent(this, ContactsSyncService.class));
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onContactsSync(ContactsReadEvent event) {
    mContactsAdapter = new ContactsAdapter(event.getContacts());
    mAlvContacts.setAdapter(mContactsAdapter);

    hideProgressBar();
  }
}