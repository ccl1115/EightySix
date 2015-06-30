package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.circle.event.CircleFollowsChangedEvent;
import com.utree.eightysix.app.region.event.CircleResponseEvent;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.FollowCircle;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.response.FollowCircleListResponse;
import com.utree.eightysix.rest.OnResponse2;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class FollowCirclesFragment extends BaseFragment {

  @InjectView (R.id.ll_set_current)
  public LinearLayout mLlSetCurrent;

  @InjectView (R.id.ll_current)
  public LinearLayout mLlCurrent;

  @InjectView (R.id.tv_current)
  public TextView mTvCurrent;

  @InjectView (R.id.ll_add_follow)
  public LinearLayout mLlAddFollow;

  @InjectView (R.id.ll_follow_circles)
  public LinearLayout mLlFollowCircles;

  private List<View> mFollowCircleViews = new ArrayList<View>();

  private Callback mCallback;

  @OnClick (R.id.fl_follow_circles)
  public void onFlFollowCirclesClicked() {
    hideSelf();
  }

  @OnClick (R.id.tv_set_current)
  public void onTvSetCurrent() {
    BaseCirclesActivity.startSelect(getActivity(), true);
  }

  @OnClick (R.id.tv_add_follow)
  public void onTvAddFollow() {
    BaseCirclesActivity.startMyCircles(getActivity());
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.widget_follow_circles, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    requestFollowCircles();

    final Circle currentCircle = Account.inst().getCurrentCircle();
    if (currentCircle != null) {
      clearSelectedCircle();
      mLlSetCurrent.setVisibility(View.GONE);
      mLlCurrent.setVisibility(View.VISIBLE);
      mTvCurrent.setText(currentCircle.shortName);
      mTvCurrent.setSelected(true);
      mTvCurrent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            if (mCallback != null) {
              mCallback.onCurrentCircleClicked(currentCircle);
            }
            hideSelf();
          }
        }
      });
    }
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  @Subscribe
  public void onCircleResponseEvent(CircleResponseEvent event) {
    if (event.getCircle() != null) {
      for (View view : mFollowCircleViews) {
        if (view.getTag() != null) {
          if (((FollowCircle) view.getTag()).factoryId == event.getCircle().id) {
            view.setSelected(true);
            break;
          }
        }
      }
    }
  }

  @Subscribe
  public void onCircleFollowsChangedEvent(CircleFollowsChangedEvent event) {
    requestFollowCircles();
  }

  private void clearSelectedCircle() {
    mTvCurrent.setSelected(false);

    for (View v : mFollowCircleViews) {
      v.setSelected(false);
    }
  }

  @Subscribe
  public void onCurrentCircleResponseEvent(final CurrentCircleResponseEvent event) {
    if (event.getCircle() != null && event.getCircle().id != 0) {
      clearSelectedCircle();
      mLlSetCurrent.setVisibility(View.GONE);
      mLlCurrent.setVisibility(View.VISIBLE);
      mTvCurrent.setText(event.getCircle().shortName);
      mTvCurrent.setSelected(true);
      mTvCurrent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            if (mCallback != null) {
              mCallback.onCurrentCircleClicked(event.getCircle());
            }
            hideSelf();
          }
        }
      });
    }
  }

  private void requestFollowCircles() {
    U.request("follow_circle_list", new OnResponse2<FollowCircleListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FollowCircleListResponse response) {

        if (response.object.size() > 0) {
          mLlAddFollow.setVisibility(View.GONE);
        } else {
          mLlAddFollow.setVisibility(View.VISIBLE);
        }

        if (response.object.size() % 3 == 1) {
          response.object.add(null);
          response.object.add(null);
        } else if (response.object.size() % 3 == 2) {
          response.object.add(null);
        }

        mLlFollowCircles.removeAllViews();

        for (int i = 0, size = response.object.size(); i < size; i += 3) {
          buildFollowCircleRow(new FollowCircle[]{
              response.object.get(i),
              response.object.get(i + 1),
              response.object.get(i + 2)
          });
        }
      }
    }, FollowCircleListResponse.class);
  }

  private void buildFollowCircleRow(final FollowCircle[] circles) {

    LinearLayout linearLayout = new LinearLayout(getActivity());
    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

    int m = U.dp2px(8);
    if (circles[0] != null) {
      TextView textView = new TextView(getActivity());
      textView.setSingleLine();
      textView.setLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setText(circles[0].factoryName);
      textView.setTag(circles[0]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, U.dp2px(30), 1);
      params.setMargins(0, m, m, 0);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));
      textView.setTag(circles[0]);

      textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            if (mCallback != null) {
              mCallback.onFollowCircleClicked((FollowCircle) v.getTag());
            }
            hideSelf();
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    }
    if (circles[1] != null) {
      TextView textView = new TextView(getActivity());
      textView.setSingleLine();
      textView.setLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setText(circles[1].factoryName);
      textView.setTag(circles[1]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, U.dp2px(30), 1);
      params.setMargins(m, m, m, 0);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));
      textView.setTag(circles[1]);

      textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            if (mCallback != null) {
              mCallback.onFollowCircleClicked((FollowCircle) v.getTag());
            }
            hideSelf();
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    } else {
      View view = new View(getActivity());
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 1, 1);
      params.setMargins(m, m, m, 0);
      view.setLayoutParams(params);
      linearLayout.addView(view);
    }

    if (circles[2] != null) {
      TextView textView = new TextView(getActivity());
      textView.setSingleLine();
      textView.setLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setText(circles[2].factoryName);
      textView.setTag(circles[2]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, U.dp2px(30), 1);
      params.setMargins(m, m, 0, 0);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));
      textView.setTag(circles[2]);

      textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            if (mCallback != null) {
              mCallback.onFollowCircleClicked((FollowCircle) v.getTag());
            }
            hideSelf();
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    } else {
      View view = new View(getActivity());
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 1, 1);
      params.setMargins(m, m, 0, 0);
      view.setLayoutParams(params);
      linearLayout.addView(view);
    }

    mLlFollowCircles.addView(linearLayout);
  }

  private void clearFollowCircleViews() {
    mTvCurrent.setSelected(false);
    for (View view : mFollowCircleViews) {
      view.setSelected(false);
    }
  }

  public interface Callback {

    void onFollowCircleClicked(FollowCircle circle);

    void onCurrentCircleClicked(Circle circle);
  }
}