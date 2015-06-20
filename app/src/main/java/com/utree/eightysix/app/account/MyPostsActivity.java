/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.event.RequireRefreshEvent;
import com.utree.eightysix.response.UserSetupResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TitleTab;

/**
 */
@Layout(R.layout.activity_my_posts)
@TopTitle(R.string.my_posts)
public class MyPostsActivity extends BaseActivity {

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  private MyAnonymousPostsFragment mMyAnonymousPostsFragment = new MyAnonymousPostsFragment() {

    @Subscribe
    public void onRequireRefreshEvent(RequireRefreshEvent event) {
      if (event.getRequestCode() == RequireRefreshEvent.REQUEST_CODE_MY_POSTS) {
        mPage = 1;
        requestPosts();
      }
    }

  };

  private MyRealNamePostsFragment mMyRealNamePostsFragment = new MyRealNamePostsFragment() {

    @Subscribe
    public void onRequireRefreshEvent(RequireRefreshEvent event) {
      if (event.getRequestCode() == RequireRefreshEvent.REQUEST_CODE_MY_POSTS) {
        mPage = 1;
        requestPosts();
      }
    }

  };

  private String mStatus;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if ("on".equals(mStatus)) {
          getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_privacy_off));
          mStatus = "off";
          U.request("user_setup_replace", new OnResponse2<Response>() {
            @Override
            public void onResponseError(Throwable e) {

            }

            @Override
            public void onResponse(Response response) {
              if (!RESTRequester.responseOk(response)) {
                getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_privacy_on));
              }
            }
          }, Response.class, "postPrivacy", "off");
        } else {
          showTurnOnPrivacyDialog();
        }

      }
    });

    U.request("user_setup", new OnResponse2<UserSetupResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(UserSetupResponse response) {
        if (RESTRequester.responseOk(response)) {
          mStatus = response.object.status;
          if ("on".equals(mStatus)) {
            getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_privacy_on));
          } else {
            getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_privacy_off));
          }
        }
      }
    }, UserSetupResponse.class, "postPrivacy");

    mVpTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return mMyAnonymousPostsFragment;
          case 1:
            return mMyRealNamePostsFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 2;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        switch (position) {
          case 0:
            return "匿名帖";
          case 1:
            return "非匿名帖";
        }
        return null;
      }
    });

    mTtTab.setViewPager(mVpTab);

    mTtTab.setOnPageChangedListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        switch (position) {
          case 0:
            mMyAnonymousPostsFragment.setActive(true);
          case 1:
            mMyRealNamePostsFragment.setActive(true);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mMyAnonymousPostsFragment.setActive(true);
      }
    }, 500);
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

  private void showTurnOnPrivacyDialog() {
    final ThemedDialog dialog = new ThemedDialog(this);

    dialog.setTitle("隐私锁");

    TextView view = new TextView(this);
    view.setText("隐私锁上锁后，其他人访问你的个人主页时，将隐藏你发表的所有帖子；锁被开启时，将只隐藏你的匿名帖！");
    view.setEms(15);
    final int px = U.dp2px(16);
    view.setPadding(px, px, px, px);

    dialog.setContent(view);

    dialog.setPositive("上锁", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_privacy_on));
        mStatus = "on";
        dialog.dismiss();
        U.request("user_setup_replace", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (!RESTRequester.responseOk(response)) {
              getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_privacy_off));
            }
          }
        }, Response.class, "postPrivacy", "on");
      }
    });

    dialog.show();
  }
}