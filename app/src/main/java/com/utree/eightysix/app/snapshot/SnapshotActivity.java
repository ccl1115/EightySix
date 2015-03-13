/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.snapshot;

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
import com.google.gson.annotations.SerializedName;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ScrollableTitleTab;
import com.utree.eightysix.widget.TopBar;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Layout(R.layout.activity_snapshot)
public class SnapshotActivity extends BaseActivity {

  @InjectView(R.id.tt_tab)
  public ScrollableTitleTab mTitleTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  private Circle mCircle;

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, SnapshotActivity.class);

    intent.putExtra("circle", circle);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void start(Context context, int circleId) {
    Intent intent = new Intent(context, SnapshotActivity.class);

    intent.putExtra("circleId", circleId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mCircle = getIntent().getParcelableExtra("circle");

    if (mCircle == null) {
      int circleId = getIntent().getIntExtra("circleId", -1);

      if (circleId == -1) {
        mCircle = new Circle();
        mCircle.id = circleId;
        finish();
        return;
      }
    }

    onNewIntent(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    setTopTitle(mCircle.shortName);

    requestSnapshotList();

    showProgressBar(true);
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


  private void requestSnapshotList() {
    U.request("feed_snapshot_list", new OnResponse2<SnapshotListResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
        finish();
      }

      @Override
      public void onResponse(SnapshotListResponse response) {
        hideProgressBar();

        if (RESTRequester.responseOk(response)) {

          if (response.object.list.size() == 0) {
            showToast("该工厂没有快照数据", false);
            finish();
            return;
          }

          mCircle = response.object.circle;

          setTopTitle(mCircle.shortName);

          buildFragments(response.object.list);

          mTopBar.setActionAdapter(new TopBar.ActionAdapter() {
            @Override
            public String getTitle(int position) {
              return "动态";
            }

            @Override
            public Drawable getIcon(int position) {
              return null;
            }

            @Override
            public Drawable getBackgroundDrawable(int position) {
              return new RoundRectDrawable(dp2px(2), getResources().getColorStateList(R.color.apptheme_primary_btn_light));
            }

            @Override
            public void onClick(View view, int position) {
              if (mCircle.currFactory == 1) {
                HomeActivity.start(SnapshotActivity.this, 0);
              } else {
                FeedActivity.start(SnapshotActivity.this, mCircle);
              }
            }

            @Override
            public int getCount() {
              return 1;
            }

            @Override
            public TopBar.LayoutParams getLayoutParams(int position) {
              return new TopBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                  ViewGroup.LayoutParams.MATCH_PARENT);
            }
          });

        } else {
          finish();
        }

      }
    }, SnapshotListResponse.class, mCircle.id);
  }

  @Keep
  public static class SnapshotListResponse extends Response {

    @SerializedName("object")
    public SnapshotList object;
  }

  @Keep
  public static class SnapshotList {

    @SerializedName("list")
    public List<Snapshot> list;

    @SerializedName("factoryView")
    public Circle circle;
  }

  @Keep
  public static class Snapshot {

    @SerializedName("id")
    public int id;

    @SerializedName("content")
    public String content;
  }

  private void buildFragments(final List<Snapshot> list) {
    final List<SnapshotFragment> fragments = new ArrayList<SnapshotFragment>(list.size());

    for (Snapshot snapshot : list) {
      SnapshotFragment fragment = new SnapshotFragment();
      fragment.setFactoryId(mCircle.id);
      fragment.setSnapshot(snapshot.id);
      fragments.add(fragment);
    }

    mVpTab.setOffscreenPageLimit(2);

    FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        return fragments.get(position);
      }

      @Override
      public int getCount() {
        return list.size();
      }

      @Override
      public CharSequence getPageTitle(int position) {
        return list.get(position).content;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        ((BaseFragment) object).setActive(false);
      }
    };

    mVpTab.setAdapter(pagerAdapter);

    mTitleTab.setViewPager(mVpTab);

    mTitleTab.setOnPageChangedListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        fragments.get(position).setActive(true);
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });


    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        fragments.get(0).setActive(true);
      }
    }, 500);
  }
}
