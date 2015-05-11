/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.data.Hometown;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.response.HometownResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 */
public class SelectAreaFragment extends BaseFragment {

  private static final String[] PROVINCE = {
      "不限", "北京", "青海", "广东", "辽宁",
      "内蒙古", "云南", "广西", "浙江",
      "四川", "香港", "宁夏", "甘肃",
      "山西", "上海", "吉林", "西藏",
      "安徽", "江苏", "澳门", "新疆",
      "陕西", "贵州", "天津", "黑龙江",
      "海南", "江西", "湖南", "湖北",
      "重庆", "河南", "河北", "福建",
      "山东", "台湾"
  };

  @InjectView(R.id.ll_parent)
  public LinearLayout mLlParent;

  @InjectView(R.id.sp_province)
  public Spinner mSpProvince;

  @InjectView(R.id.sp_city)
  public Spinner mSpCity;

  @InjectView(R.id.tv_title)
  public TextView mTvTitle;

  @InjectView(R.id.sp_county)
  public Spinner mSpCounty;

  @InjectView(R.id.progress_bar)
  public FrameLayout mFlProgressBar;

  @InjectView(R.id.rb_settings)
  public RoundedButton mRbSettings;

  private Callback mCallback;

  private int mAreaType;
  private int mAreaId;
  private String mAreaName;
  private List<Hometown> mAreas;


  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    if (!isDetached()) {
      getFragmentManager().beginTransaction()
          .detach(this).commit();
    }
  }

  @OnClick(R.id.rb_settings)
  public void onRbSettingsClicked() {
    detachSelf();
    if (mCallback != null) {
      mCallback.onAreaSelected(mAreaType, mAreaId, mAreaName);
    }
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_set_hometown, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    view.setFocusableInTouchMode(true);
    view.setFocusable(true);

    view.requestFocus();

    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
          if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
            detachSelf();
          }
          return true;
        } else {
          return false;
        }
      }
    });

    mTvTitle.setText("选择地区");
    mRbSettings.setText("确认");

    mLlParent.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(8), Color.WHITE));

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, PROVINCE);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSpProvince.setAdapter(arrayAdapter);

    requestArea();
  }

  private void requestCities(int provinceId) {
    mFlProgressBar.setVisibility(View.VISIBLE);
    U.request("get_cities", new OnResponse2<HometownResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final HometownResponse response) {
        mFlProgressBar.setVisibility(View.GONE);
        if (RESTRequester.responseOk(response)) {
          Hometown unset = new Hometown();
          unset.id = -1;
          unset.name = "不限";
          response.object.lists.add(0, unset);
          ArrayAdapter<Hometown> adapter =
              new ArrayAdapter<Hometown>(getActivity(), android.R.layout.simple_spinner_item, response.object.lists);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          mSpCity.setAdapter(adapter);
          mSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              if (i == 0) {
                mSpCounty.setAdapter(null);
                mAreaName = (String) mSpProvince.getSelectedItem();
                mAreaType = 1;
                mAreaId = mSpProvince.getSelectedItemPosition();
              } else {
                requestCounties(response.object.lists.get(i).id);
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
          });

          int index = 0;
          for (Hometown hometown : response.object.lists) {
            if (hometown.id == mAreas.get(1).id) {
              mSpCity.setSelection(index);
            }
            index++;
          }
        }
      }
    }, HometownResponse.class, provinceId);
  }

  private void requestCounties(int cityId) {
    mFlProgressBar.setVisibility(View.VISIBLE);
    U.request("get_counties", new OnResponse2<HometownResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mFlProgressBar.setVisibility(View.GONE);
      }

      @Override
      public void onResponse(HometownResponse response) {
        mFlProgressBar.setVisibility(View.GONE);
        if (RESTRequester.responseOk(response)) {
          Hometown unset = new Hometown();
          unset.id = -1;
          unset.name = "不限";
          response.object.lists.add(0, unset);
          ArrayAdapter<Hometown> adapter =
              new ArrayAdapter<Hometown>(getActivity(), android.R.layout.simple_spinner_item, response.object.lists);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          mSpCounty.setAdapter(adapter);
          mSpCounty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              if (position == 0) {
                mAreaName = ((Hometown) mSpCity.getSelectedItem()).name;
                mAreaId = ((Hometown) mSpCity.getSelectedItem()).id;
                mAreaType = 2;
              } else {
                mAreaName = ((Hometown) mSpCounty.getAdapter().getItem(position)).name;
                mAreaId = ((Hometown) mSpCounty.getAdapter().getItem(position)).id;
                mAreaType = 2;
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
          });

          int index = 0;
          for (Hometown hometown : response.object.lists) {
            if (hometown.id == mAreas.get(2).id) {
              mSpCounty.setSelection(index);
            }
            index++;
          }
        }
      }
    }, HometownResponse.class, cityId);
  }

  private void requestArea() {
    U.request("feed_get_area", new OnResponse2<AreaResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final AreaResponse response) {
        mSpProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
              mSpCity.setAdapter(null);
              mSpCounty.setAdapter(null);
              mAreaId = 0;
              mAreaType = -1;
              mAreaName = "全国";
            } else {
              requestCities(position);
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
        });
        mAreas = response.object.areas;
        mSpProvince.setSelection(mAreas.get(0).id);
        requestCities(mAreas.get(0).id);
      }
    }, AreaResponse.class);
  }

  @Override
  public boolean onBackPressed() {
    if (!isDetached()) {
      getFragmentManager().beginTransaction()
          .detach(this).commit();
      return true;
    } else {
      return super.onBackPressed();
    }
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public interface Callback {
    void onAreaSelected(int areaType, int areaId, String areaName);
  }

  @Keep
  public static class AreaResponse extends Response {

    @SerializedName("object")
    public Area object;
  }

  public static class Area {

    @SerializedName("lists")
    public List<Hometown> areas;
  }
}
