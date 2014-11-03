package com.utree.eightysix.app.tag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.share.ShareManager;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.widget.TitleTab;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout(R.layout.activity_tag_tab)
public class TagTabActivity extends BaseActivity {

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  private FactoryTagFragment mFactoryTagFragment;
  private HotTagFragment mHotTagFragment;
  private MoreTagFragment mMoreTagFragment;
  private Tag mTag;

  public static void start(Context context, Tag tag) {
    Intent i = new Intent(context, TagTabActivity.class);

    i.putExtra("tag", tag);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(i);
  }

  public static void start(Context context, String id) {
    Intent i = new Intent(context, TagTabActivity.class);

    i.putExtra("id", id);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(i);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mFactoryTagFragment = new FactoryTagFragment();
    mHotTagFragment = new HotTagFragment();
    mMoreTagFragment = new MoreTagFragment();

    showRefreshIndicator();


    mVpTab.setOffscreenPageLimit(2);

    mVpTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return mHotTagFragment;
          case 1:
            return mFactoryTagFragment;
          case 2:
            return mMoreTagFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 3;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        switch (position) {
          case 0:
            return "热门";
          case 1:
            return "同厂";
          case 2:
            return "更多";
        }
        return "";
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
            mHotTagFragment.setActive(true);
            break;
          case 1:
            mFactoryTagFragment.setActive(true);
            break;
          case 2:
            mMoreTagFragment.setActive(true);
            break;
        }

        U.getAnalyser().trackEvent(U.getContext(), "tag_tab_switch", String.valueOf(position));
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return null;
      }

      @Override
      public Drawable getIcon(int position) {
        return getResources().getDrawable(R.drawable.ic_topbar_share);
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_transparent_bg);
      }

      @Override
      public void onClick(View view, int position) {
        U.getShareManager().shareTagDialog(TagTabActivity.this, null, mTag.id);
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }
    });

    onNewIntent(getIntent());

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mHotTagFragment.setActive(true);
      }
    }, 500);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    mTag = intent.getParcelableExtra("tag");
    setTopTitle("#" + mTag.content);
    mFactoryTagFragment.setTag(mTag);
    mHotTagFragment.setTag(mTag);

    mVpTab.setCurrentItem(0);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}