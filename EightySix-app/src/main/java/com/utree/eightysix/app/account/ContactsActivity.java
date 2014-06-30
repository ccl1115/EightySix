package com.utree.eightysix.app.account;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.contact.Contact;
import com.utree.eightysix.contact.ContactsReadEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TopBar;

/**
 * @author simon
 */
@Layout (R.layout.activity_contacts)
@TopTitle (R.string.who_to_invite)
public class ContactsActivity extends BaseActivity {


  @InjectView (R.id.alv_contacts)
  public AdvancedListView mAlvContacts;

  @InjectView (R.id.tv_empty_text)
  public TextView mTvEmptyView;

  @InjectView(R.id.rb_search_hint)
  public RoundedButton mRbSearchHint;

  private ContactsAdapter mContactsAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mRbSearchHint.setText(getString(R.string.search_contact));

    showProgressBar();

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return getString(R.string.done);
      }

      @Override
      public Drawable getIcon(int position) {
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
      }

      @Override
      public void onClick(View view, int position) {
        for (Contact contact : mContactsAdapter.getChecked()) {
          sendSMS(contact.phone, "来自蓝莓");
        }
        finish();
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public ViewGroup.LayoutParams getLayoutParams(int position) {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }
    });

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
    mAlvContacts.setEmptyView(mTvEmptyView);
    mContactsAdapter = new ContactsAdapter(event.getContacts());
    mAlvContacts.setAdapter(mContactsAdapter);

    if (event.getContacts() == null) {
      mTvEmptyView.setText(getString(R.string.no_permission_to_read_contacts));
    } else if (event.getContacts().size() == 0) {
      mTvEmptyView.setText(getString(R.string.contacts_empty));
    }

    hideProgressBar();
  }

  private void sendSMS(String phoneNumber, String message) {
    PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, ContactsActivity.class), 0);
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(phoneNumber, null, message, null, null);
  }

}