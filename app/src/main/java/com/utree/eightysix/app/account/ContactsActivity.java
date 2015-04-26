package com.utree.eightysix.app.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.contact.Contact;
import com.utree.eightysix.data.UnregContacts;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.UnregContactsRequest;
import com.utree.eightysix.response.UnregContactsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.TextActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
@Layout(R.layout.activity_contacts)
@TopTitle(R.string.who_to_invite)
public class ContactsActivity extends BaseActivity {


  private static final String TAG = "ContactsActivity";

  @InjectView(R.id.alv_contacts)
  public AdvancedListView mAlvContacts;

  @InjectView(R.id.tv_empty_text)
  public TextView mTvEmptyView;

  @InjectView(R.id.tv_search_hint)
  public EditText mEtSearchHint;

  private ContactsAdapter mContactsAdapter;

  public static void start(Context context, String textToShare) {
    Intent intent = new Intent(context, ContactsActivity.class);
    intent.putExtra("textToShare", textToShare);
    context.startActivity(intent);
  }

  @Subscribe
  public void onContactCheckedChanged(ContactCheckedCountChanged changed) {
    if (changed.getCount() == 0) {
      disableSendButton();
    } else {
      enableSendButton();
    }
  }

  @OnTextChanged(R.id.tv_search_hint)
  public void onEtSearchHintTextChanged(CharSequence cs) {
    if (mContactsAdapter != null) {
      mContactsAdapter.setFilter(cs);
    }
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mEtSearchHint.setInputType(InputType.TYPE_CLASS_TEXT);
    mEtSearchHint.setHint(R.string.search_contact);
    mEtSearchHint.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(2), Color.WHITE));

    showProgressBar();

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().getAbRight().setText(getString(R.string.send));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mContactsAdapter == null) return;
        for (Contact contact : mContactsAdapter.getChecked()) {
          String textToShare = getIntent().getStringExtra("textToShare");
          sendSMS(contact.phone, textToShare);
        }

        showToast(getString(R.string.share_succeed), false);
        finish();
      }
    });

    requestUnregContacts();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void sendSMS(String phoneNumber, String message) {
    if (message == null) return;
    SmsManager sms = SmsManager.getDefault();
    ArrayList<String> strings = SmsManager.getDefault().divideMessage(message);
    sms.sendMultipartTextMessage(phoneNumber, null, strings, null, null);
  }

  public static class ContactCheckedCountChanged {
    private int mCount;

    public ContactCheckedCountChanged(int count) {
      mCount = count;
    }

    public int getCount() {
      return mCount;
    }
  }

  protected void enableSendButton() {
    mTopBar.getActionView(0).setEnabled(true);
    mTopBar.getActionView(0).setActionBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(2),
            getResources().getColorStateList(R.color.apptheme_primary_btn_light)));
    ((TextActionButton) mTopBar.getActionView(0)).setTextColor(Color.WHITE);
  }

  private void disableSendButton() {
    mTopBar.getActionView(0).setEnabled(false);
    mTopBar.getActionView(0).setActionBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(2),
            getResources().getColor(R.color.apptheme_primary_light_color_disabled)));
    ((TextActionButton) mTopBar.getActionView(0)).setTextColor(
        getResources().getColor(R.color.apptheme_primary_grey_color_disabled));
  }

  private void requestUnregContacts() {
    request(new UnregContactsRequest(), new OnResponse2<UnregContactsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

        hideProgressBar();
      }

      @Override
      public void onResponse(UnregContactsResponse response) {
        if (RESTRequester.responseOk(response)) {
          List<Contact> contacts = new ArrayList<Contact>();
          for (UnregContacts.UnregContact contact : response.object.unregContacts) {
            Contact c = new Contact();
            c.name = contact.name;
            c.phone = contact.phone;
            contacts.add(c);
          }

          if (contacts.size() == 0) {
            mTvEmptyView.setText(getString(R.string.contacts_empty));
          }

          mContactsAdapter = new ContactsAdapter(contacts);
          mAlvContacts.setAdapter(mContactsAdapter);
        }

        hideProgressBar();
      }
    }, UnregContactsResponse.class);
  }
}