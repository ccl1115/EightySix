/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.hometown;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.data.Hometown;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.response.HometownResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ThemedDialog;

/**
 */
public class SetHometownFragment extends BaseFragment {

  private static final String[] PROVINCE = {
      "北京", "青海", "广东", "辽宁",
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
  private Callback mCallback;

  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    if (!isDetached()) {
      getFragmentManager().beginTransaction()
          .detach(this).commit();
    }
  }

  @OnClick(R.id.rb_settings)
  public void onRbSettingsClicked() {
    showConfirmDialog();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_set_hometown, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    Bundle arguments = getArguments();
    if (arguments != null) {
      mTvTitle.setText(arguments.getString("title"));
    }

    mLlParent.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(8), Color.WHITE));

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, PROVINCE);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSpProvince.setAdapter(arrayAdapter);

    mSpProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        if (mSpCity.getAdapter() != null) mSpCity.setAdapter(null);
        requestCities(i + 1);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
  }

  private void requestCities(int provinceId) {
    U.request("get_cities", new OnResponse2<HometownResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final HometownResponse response) {
        if (RESTRequester.responseOk(response)) {
          ArrayAdapter<Hometown> adapter =
              new ArrayAdapter<Hometown>(getActivity(), android.R.layout.simple_spinner_item, response.object.lists);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          mSpCity.setAdapter(adapter);
          mSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              requestCounties(response.object.lists.get(i).id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
          });
        }
      }
    }, HometownResponse.class, provinceId);
  }

  private void requestCounties(int cityId) {
    U.request("get_counties", new OnResponse2<HometownResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(HometownResponse response) {
        if (RESTRequester.responseOk(response)) {
          ArrayAdapter<Hometown> adapter =
              new ArrayAdapter<Hometown>(getActivity(), android.R.layout.simple_spinner_item, response.object.lists);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          mSpCounty.setAdapter(adapter);
        }
      }
    }, HometownResponse.class, cityId);
  }

  private void requestSetHometown() {
    U.request("set_hometown", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              if (mCallback != null) {
                mCallback.onHometownSet(((Hometown) mSpCounty.getSelectedItem()).id);
              }
              if (!isDetached()) {
                getFragmentManager().beginTransaction()
                    .detach(SetHometownFragment.this).commit();
              }
            }
          }
        }, Response.class,
        mSpProvince.getSelectedItem() == null ? null : mSpProvince.getSelectedItemPosition() + 1,
        mSpCity.getSelectedItem() == null ? null : ((Hometown) mSpCity.getSelectedItem()).id,
        mSpCounty.getSelectedItem() == null ? null : ((Hometown) mSpCounty.getSelectedItem()).id);
  }

  private void showConfirmDialog() {
    final ThemedDialog dialog = new ThemedDialog(getActivity());

    final String title = String.format("确认你在%s%s%s么？",
        mSpProvince.getSelectedItem() == null ? "" : mSpProvince.getSelectedItem(),
        mSpCity.getSelectedItem() == null ? "" : mSpCity.getSelectedItem(),
        mSpCounty.getSelectedItem() == null ? "" : mSpCounty.getSelectedItem());

    TextView view = new TextView(getActivity());
    view.setText("提醒：确认后15天内不可修改哦！");
    final int p = U.dp2px(16);
    view.setPadding(p, p, p, p);
    dialog.setContent(view);

    dialog.setTitle(title);

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        requestSetHometown();
        dialog.dismiss();
      }
    });

    dialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    dialog.show();
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
    void onHometownSet(int hometownId);
  }
}
