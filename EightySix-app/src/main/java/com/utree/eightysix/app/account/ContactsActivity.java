package com.utree.eightysix.app.account;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.InjectView;
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
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.TextActionButton;
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

  @InjectView (R.id.tv_search_hint)
  public EditText mRbSearchHint;

  private ContactsAdapter mContactsAdapter;

  public static void start(Context context, String textToShare) {
    Intent intent = new Intent(context, ContactsActivity.class);
    intent.putExtra("textToShare", textToShare);
    context.startActivity(intent);
  }

  @Subscribe
  public void onContactCheckedChanged(ContactCheckedCountChanged changed) {
    TextActionButton actionView = (TextActionButton) getTopBar().getActionView(0);
    if (changed.getCount() == 0) {
      actionView.setEnabled(false);
      actionView.setText("完成");
    } else {
      actionView.setEnabled(true);
      actionView.setText(String.format("完成(%d)", changed.getCount()));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mRbSearchHint.setHint(R.string.search_contact);
    mRbSearchHint.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(2), Color.WHITE));

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
          sendSMS(contact.phone, getIntent().getStringExtra("textToShare"));
        }

        showToast(getString(R.string.share_succeed), false);
        finish();
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public FrameLayout.LayoutParams getLayoutParams(int position) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        layoutParams.rightMargin = U.dp2px(8);
        return layoutParams;
      }
    });

    getTopBar().getActionView(0).setEnabled(false);

    ContactsSyncService.start(this, true);
  }

  @Override
  public void onActionLeftClicked() {
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

  public static class ContactCheckedCountChanged {
    private int mCount;

    public ContactCheckedCountChanged(int count) {
      mCount = count;
    }

    public int getCount() {
      return mCount;
    }
  }

}