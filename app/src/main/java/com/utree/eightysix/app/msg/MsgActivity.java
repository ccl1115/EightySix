package com.utree.eightysix.app.msg;

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
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.widget.TitleTab;

/**
 * @author simon
 */
@Layout (R.layout.activity_msg)
@TopTitle (R.string.messages)
public class MsgActivity extends BaseActivity {

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  private BaseMsgFragment mMyPostMsgFragment = new BaseMsgFragment() {
    @Override
    protected int getCreateType() {
      return 1;
    }
  };

  private BaseMsgFragment mOtherMsgFragment = new BaseMsgFragment() {
    @Override
    protected int getCreateType() {
      return 0;
    }
  };

  public static void start(Context context, boolean refresh) {
    Intent intent = new Intent(context, MsgActivity.class);
    intent.putExtra("refresh", refresh);
    context.startActivity(intent);
  }

  public static Intent getIntent(Context context, boolean refresh) {
    Intent intent = new Intent(context, MsgActivity.class);
    intent.putExtra("refresh", refresh);
    return intent;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mVpTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        if (position == 0) {
          return mMyPostMsgFragment;
        } else if (position == 1) {
          return mOtherMsgFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 2;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        if (position == 0) {
          return "我发表的";
        } else if (position == 1) {
          return "我关注的";
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
        if (position == 0) {
          mMyPostMsgFragment.setActive(true);
        } else if (position == 1) {
          mOtherMsgFragment.setActive(true);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mMyPostMsgFragment.setActive(true);
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

}