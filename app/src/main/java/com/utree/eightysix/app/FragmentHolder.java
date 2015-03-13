/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;

/**
 */
@Layout(R.layout.activity_fragment_holder)
public class FragmentHolder extends BaseActivity {

  private HolderFragment mFragment;

  public static <T extends HolderFragment> void start(Context context, Class<T> clz) {
    Intent i = new Intent(context, FragmentHolder.class);

    i.putExtra("fragmentClass", clz);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(i);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Class clz = (Class) getIntent().getSerializableExtra("fragmentClass");

    try {
      mFragment = (HolderFragment) clz.newInstance();

      getSupportFragmentManager().beginTransaction()
          .add(R.id.content, mFragment)
          .commit();
    } catch (InstantiationException e) {
      finish();
    } catch (IllegalAccessException e) {
      finish();
    }


  }

  @Override
  public void onTitleClicked() {
    mFragment.onTitleClicked();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

}