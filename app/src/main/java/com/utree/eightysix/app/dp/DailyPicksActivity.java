package com.utree.eightysix.app.dp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.TitleTab;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Layout(R.layout.activity_daily_picks)
@TopTitle(R.string.daily_picks)
public class DailyPicksActivity extends BaseActivity {


  public static void start(Context context, int index) {
    Intent intent = new Intent(context, DailyPicksActivity.class);

    intent.putExtra("index", index);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  private List<DailyPicksFragment> mDailyPicksFragments = new ArrayList<DailyPicksFragment>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestTags();
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

  private void requestTags() {
    U.request("daily_picks_tags", new OnResponse2<TagsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final TagsResponse response) {

        mVpTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
          @Override
          public Fragment getItem(final int position) {
            DailyPicksFragment dailyPicksFragment = new DailyPicksFragment() {
              @Override
              protected Tag getRequestTag() {
                return response.object.tags.get(position);
              }
            };

            mDailyPicksFragments.add(dailyPicksFragment);
            return dailyPicksFragment;
          }

          @Override
          public int getCount() {
            return response.object.tags.size();
          }

          @Override
          public CharSequence getPageTitle(int position) {
            return response.object.tags.get(position).content;
          }
        });

        mTtTab.setViewPager(mVpTab);

        mTtTab.setOnPageChangedListener(new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

          }

          @Override
          public void onPageSelected(int position) {
            mDailyPicksFragments.get(position).setActive(true);
          }

          @Override
          public void onPageScrollStateChanged(int state) {

          }
        });

        getHandler().postDelayed(new Runnable() {
          @Override
          public void run() {
            int index = getIntent().getIntExtra("index", 0);
            if (index == 0) {
              mDailyPicksFragments.get(0).setActive(true);
            } else {
              mVpTab.setCurrentItem(Math.min(mVpTab.getAdapter().getCount() - 1, index));
            }
          }
        }, 500);
      }
    }, TagsResponse.class);
  }
}