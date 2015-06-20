package com.utree.eightysix.app.dp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.ScrollableTitleTab;

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
  public ScrollableTitleTab mTtTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

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
        final List<Tag> tags = response.object.tags;
        final List<DailyPicksFragment> fragments = new ArrayList<DailyPicksFragment>(tags.size());

        for (final Tag tag : tags) {
          DailyPicksFragment fragment = new DailyPicksFragment() {
            @Override
            protected Tag getRequestTag() {
              return tag;
            }
          };
          fragments.add(fragment);
        }

        mVpTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
          @Override
          public Fragment getItem(final int position) {
            return fragments.get(position);
          }

          @Override
          public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);

            ((BaseFragment) object).setActive(false);
          }

          @Override
          public int getCount() {
            return tags.size();
          }

          @Override
          public CharSequence getPageTitle(int position) {
            return tags.get(position).content;
          }
        });

        mTtTab.setViewPager(mVpTab);

        mTtTab.setOnPageChangedListener(new ViewPager.OnPageChangeListener() {
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
            int index = getIntent().getIntExtra("index", 0);
            if (index == 0) {
              fragments.get(0).setActive(true);
            } else {
              mVpTab.setCurrentItem(Math.min(mVpTab.getAdapter().getCount() - 1, index), false);
            }
          }
        }, 500);


      }
    }, TagsResponse.class, 0);
  }
}