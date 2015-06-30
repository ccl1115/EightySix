package com.utree.eightysix.app.region;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.account.event.PortraitUpdatedEvent;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.feed.AbsFeedsFragment;
import com.utree.eightysix.app.feed.FollowFeedsFragment;
import com.utree.eightysix.app.feed.SelectAreaFragment;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.app.feed.event.UploadClickedEvent;
import com.utree.eightysix.app.region.event.CircleResponseEvent;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.FollowCircle;
import com.utree.eightysix.response.FollowCircleListResponse;
import com.utree.eightysix.response.ProfileResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TopBar;
import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class TabRegionFragment extends BaseFragment implements AbsFeedsFragment.OnScrollCallback {

  @InjectView (R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView (R.id.fl_distance_selector)
  public FrameLayout mLlDistanceSelector;

  @InjectView (R.id.sb_distance)
  public SeekBar mSbDistance;

  @InjectView (R.id.tv_distance)
  public TextView mTvDistance;

  @InjectView (R.id.tv_area_name)
  public TextView mTvAreaName;

  @InjectView (R.id.rb_region)
  public RadioButton mRbRegion;

  @InjectView (R.id.rb_area)
  public RadioButton mRbArea;

  @InjectView (R.id.iv_select_area)
  public ImageView mIvSelectArea;

  private RegionFeedsFragment mRegionFeedsFragment;
  private FollowFeedsFragment mFollowFeedsFragment;

  private SelectAreaFragment mSelectAreaFragment;

  private ThemedDialog mNoPermDialog;
  private ThemedDialog mUnlockDialog;
  private ThemedDialog mInviteDialog;

  private String mAvatar;

  public TabRegionFragment() {
    mRegionFeedsFragment = new RegionFeedsFragment();
    mFollowFeedsFragment = new FollowFeedsFragment();

    mRegionFeedsFragment.setOnScrollCallback(this);
    mFollowFeedsFragment.setOnScrollCallback(this);
  }

  @OnClick (R.id.ib_send)
  public void onIbSendClicked() {
    if (mVpTab.getCurrentItem() == 0) {
      // #TODO publish to region PublishActivity.start(getActivity());
    }

  }


  @OnClick (R.id.fl_distance_selector)
  public void onLlDistanceSelector() {
    mLlDistanceSelector.setVisibility(View.GONE);
  }

  @OnCheckedChanged (R.id.rb_area)
  public void onRbArea(boolean checked) {
    if (checked) {
      mTvDistance.setText(mTvAreaName.getText());
      mSbDistance.setEnabled(false);
      mIvSelectArea.setEnabled(true);
    }
  }

  @OnCheckedChanged (R.id.rb_region)
  public void onRbRegion(boolean checked) {
    if (checked) {
      mTvDistance.setText(String.format("%.2fkm", mSbDistance.getProgress() / 1000f + 1));
      mSbDistance.setEnabled(true);
      mIvSelectArea.setEnabled(false);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_tab, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mSbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float value = progress / 1000f + 1;
        mTvDistance.setText(String.format("%.2fkm", value));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    })
    ;

    mVpTab.setOffscreenPageLimit(2);

    mVpTab.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return mRegionFeedsFragment;
          case 1:
            return mFollowFeedsFragment;
        }
        return null;

      }

      @Override
      public int getCount() {
        return 2;
      }
    });

    mVpTab.setCurrentItem(getArguments().getInt("tabIndex"));

    mVpTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        getBaseActivity().showTopBar(true);
        mRegionFeedsFragment.showLlSubTitle();
        mFollowFeedsFragment.showLlSubTitle();
      }

      @Override
      public void onPageSelected(int position) {
        getTopBar().setTitleTabSelected(position);

        switch (position) {
          case 0:
            mRegionFeedsFragment.setActive(true);
            break;
          case 1:
            mFollowFeedsFragment.setActive(true);
            break;
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    getBaseActivity().getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (getArguments().getInt("tabIndex") == 0) {
          getTopBar().setTitleTabSelected(0);
          mRegionFeedsFragment.setActive(true);
        }
      }
    }, 500);

    getBaseActivity().setTopTitle("");
    getBaseActivity().setTopSubTitle("");

    setTopBarTitle();

    requestProfile();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    clearActive();
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      setTopBarTitle();
    }
    mRegionFeedsFragment.onHiddenChanged(hidden);
    mFollowFeedsFragment.onHiddenChanged(hidden);
  }

  @Subscribe
  public void onUploadClicked(UploadClickedEvent event) {
    if (getBaseActivity() != null) {
      ContactsSyncService.start(getBaseActivity(), true);
      getBaseActivity().showProgressBar(true);
    }
  }

  @Subscribe
  public void onUnlockClicked(UnlockClickedEvent event) {
    if (getBaseActivity() != null) {
      showUnlockDialog();
      ContactsSyncService.start(getBaseActivity(), true);
    }
  }

  @Subscribe
  public void onInviteClicked(InviteClickedEvent event) {
    if (getBaseActivity() != null) {
      showInviteDialog();
    }
  }

  @Subscribe
  public void onRegionResponseEvent(RegionResponseEvent event) {
    if (event.getRegion() == 3) {
      mRbRegion.setChecked(true);
      mSbDistance.setProgress(10000);
    } else if (event.getRegion() == 4) {
      mRbRegion.setChecked(true);
      mSbDistance.setProgress(event.getDistance() - 1000);
    } else if (event.getRegion() == 5) {
      mRbArea.setChecked(true);
      mTvDistance.setText(event.getCityName());
    }
    mTvAreaName.setText(event.getCityName());
  }



  public boolean onBackPressed() {
    return false;
  }

  private void clearActive() {
    if (mRegionFeedsFragment != null) mRegionFeedsFragment.setActive(false);
    if (mFollowFeedsFragment != null) mFollowFeedsFragment.setActive(false);
  }

  private void showUnlockDialog() {
    if (mUnlockDialog == null) {
      mUnlockDialog = new ThemedDialog(getBaseActivity());
      View view = LayoutInflater.from(getBaseActivity()).inflate(R.layout.dialog_unlock, null);
      TextView tipView = (TextView) view.findViewById(R.id.tv_unlock_tip);
      String tip = getString(R.string.unlock_tip, U.getSyncClient().getSync().unlockFriends, U.getSyncClient().getSync().unlockFriends);
      int index = tip.indexOf("解锁条件");
      ForegroundColorSpan span = new ForegroundColorSpan(
          getResources().getColor(R.color.apptheme_primary_light_color));
      SpannableString spannableString = new SpannableString(tip);
      spannableString.setSpan(span, index, index + 4, 0);
      tipView.setText(spannableString);
      mUnlockDialog.setContent(view);
      mUnlockDialog.setPositive(R.string.invite_people, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mUnlockDialog.dismiss();
          showInviteDialog();
        }
      });
      mUnlockDialog.setTitle("帖子为什么会隐藏");
    }

    if (!mUnlockDialog.isShowing()) {
      mUnlockDialog.show();
    }
  }

  private void showInviteDialog() {
    //if (mInviteDialog == null) {
    //  mInviteDialog = U.getShareManager().shareAppDialog(getBaseActivity(), mFeedFragment.getCircle());
    //}
    //if (!mInviteDialog.isShowing()) {
    //  mInviteDialog.show();
    //}
  }

  private void setTopBarTitle() {
    getTopBar().getAbLeft().setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ProfileFragment.start(v.getContext());
          }
        });

    if (TextUtils.isEmpty(mAvatar)) {
      requestProfile();
    } else {
      loadPortrait(mAvatar);
    }

    getTopBar().setTitleAdapter(new TopBar.TitleAdapter() {
      @Override
      public String getTitle(int position) {
        if (position == 0) {
          return "附近";
        } else if (position == 1) {
          return "在职";
          //} else if (position == 2) {
          //  return "朋友";
        }
        return null;
      }

      @Override
      public void onSelected(View view, int position) {
        mVpTab.setCurrentItem(position, true);
        switch (position) {
          case 0:
            mRegionFeedsFragment.setActive(true);
            break;
          case 1:
            mFollowFeedsFragment.setActive(true);
            break;
        }
      }

      @Override
      public int getCount() {
        return 2;
      }
    });

    getTopBar().setTitleTabSelected(mVpTab.getCurrentItem());
  }



  private void requestProfile() {
    U.request("profile", new OnResponse2<ProfileResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(ProfileResponse response) {
        mAvatar = response.object.avatar;
        loadPortrait(mAvatar);
      }
    }, ProfileResponse.class, (String) null);
  }

  protected void loadPortrait(String avatar) {
    Picasso.with(getActivity()).load(avatar).resize(U.dp2px(32), U.dp2px(32)).transform(new Transformation() {
      @Override
      public Bitmap transform(Bitmap source) {
        final int sw = source.getWidth();
        final int sh = source.getHeight();
        final Bitmap output = Bitmap.createBitmap(sw,
            sh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sw, sh);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(sw / 2, sh / 2, sw / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);

        paint.setStrokeWidth(U.dp2px(1));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x88ffffff);
        paint.setXfermode(null);
        canvas.drawCircle(sw >> 1, sh >> 1, (sw - U.dp2px(1)) >> 1, paint);
        source.recycle();
        return output;
      }

      @Override
      public String key() {
        return "rounded";
      }
    }).into(new Target() {
      @Override
      public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        getTopBar().getAbLeft().setDrawable(new BitmapDrawable(getResources(), bitmap));
      }

      @Override
      public void onBitmapFailed(Drawable errorDrawable) {

      }

      @Override
      public void onPrepareLoad(Drawable placeHolderDrawable) {

      }
    });
  }

  @Subscribe
  public void onPortraitUpdatedEvent(PortraitUpdatedEvent event) {
    loadPortrait(event.getUrl());
  }

  @Override
  public void showTopBar() {
  }

  @Override
  public void hideTopBar() {
  }

}
