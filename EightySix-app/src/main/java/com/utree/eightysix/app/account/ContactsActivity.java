package com.utree.eightysix.app.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
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
import com.utree.eightysix.contact.ContactsReadEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.UnregContacts;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.UnregContactsRequest;
import com.utree.eightysix.response.UnregContactsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.TextActionButton;
import com.utree.eightysix.widget.TopBar;
import de.akquinet.android.androlog.Log;

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
        return new RoundRectDrawable(U.dp2px(2), getResources().getColorStateList(R.color.apptheme_primary_btn_light));
      }

      @Override
      public void onClick(View view, int position) {
        for (Contact contact : mContactsAdapter.getChecked()) {
          String textToShare = String.format("%s, %s", contact.name, getIntent().getStringExtra("textToShare"));
          Log.d(TAG, "send Share msg to " + contact.toString());
          Log.d(TAG, "with share msg " + textToShare);
          sendSMS(contact.phone, textToShare);
        }

        showToast(getString(R.string.share_succeed), false);
        finish();
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        TopBar.LayoutParams layoutParams = new TopBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = U.dp2px(8);
        return layoutParams;
      }
    });

    getTopBar().getActionView(0).setActionBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(2),
            getResources().getColor(R.color.apptheme_primary_light_color_disabled)));
    ((TextActionButton) getTopBar().getActionView(0)).setTextColor(
        getResources().getColor(R.color.apptheme_primary_grey_color_disabled));

    requestUnregContacts();
  }

  @Override
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
    getTopBar().getActionView(0).setEnabled(true);
    getTopBar().getActionView(0).setActionBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(2),
            getResources().getColorStateList(R.color.apptheme_primary_btn_light)));
    ((TextActionButton) getTopBar().getActionView(0)).setTextColor(Color.WHITE);
  }

  private void disableSendButton() {
    getTopBar().getActionView(0).setEnabled(false);
    getTopBar().getActionView(0).setActionBackgroundDrawable(
        new RoundRectDrawable(U.dp2px(2),
            getResources().getColor(R.color.apptheme_primary_light_color_disabled)));
    ((TextActionButton) getTopBar().getActionView(0)).setTextColor(
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