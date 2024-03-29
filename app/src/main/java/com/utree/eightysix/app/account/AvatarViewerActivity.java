/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.event.PortraitUpdatedEvent;
import com.utree.eightysix.response.UserAvatarsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AsyncImageView;

/**
 */
@Layout(R.layout.activity_avatar_viewer)
@TopTitle(R.string.avatar_viewer)
public class AvatarViewerActivity extends BaseActivity {

  public static void start(Context context, int index, int viewId) {
    Intent intent = new Intent(context, AvatarViewerActivity.class);
    intent.putExtra("index", index);
    intent.putExtra("viewId", viewId);

    if (!(context instanceof Activity)) {
      intent.putExtra("index", index);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.vp_avatars)
  public ViewPager mVpAvatars;

  @InjectView(R.id.tv_count)
  public TextView mTvCount;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_avatar_viewer);

    final int viewId = getIntent().getIntExtra("viewId", -1);

    getTopBar().getAbRight().setText("相册");
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AvatarsActivity.start(v.getContext(), viewId);
      }
    });
    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    if (viewId == -1) {
      requestAvatars(null);
    } else {
      requestAvatars(viewId);
    }

    mVpAvatars.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        mTvCount.setText(String.format("%d/%d", position + 1, mVpAvatars.getAdapter().getCount()));
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    onNewIntent(getIntent());
  }

  public void requestAvatars(final Integer viewId) {
    U.request("user_avatars", new OnResponse2<UserAvatarsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final UserAvatarsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object == null || response.object.size() == 0) {
            if (viewId != null) {
              showToast("他还没上传头像呃", false);
            } else {
              AvatarsActivity.start(AvatarViewerActivity.this, -1);
            }
            finish();
            return;
          }

          mVpAvatars.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
              return response.object.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
              return view.equals(object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
              AsyncImageView view = new AsyncImageView(container.getContext());
              String avatar = response.object.get(position).avatar;
              if (!TextUtils.isEmpty(avatar)) {
                view.setUrl(avatar);
              }
              container.addView(view);
              return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
              container.removeView((View) object);
            }
          });

          final int index = getIntent().getIntExtra("index", 0);
          mTvCount.setText(String.format("%d/%d", index + 1, response.object.size()));
          mVpAvatars.setCurrentItem(index);
        }
      }
    }, UserAvatarsResponse.class, viewId);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    mVpAvatars.setCurrentItem(intent.getIntExtra("index", 0));
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

  @Subscribe
  public void onPortraitUpdatedEvent(PortraitUpdatedEvent event) {
    requestAvatars(null);
  }
}