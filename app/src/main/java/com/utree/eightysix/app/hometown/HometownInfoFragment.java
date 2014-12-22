/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.hometown;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.response.HometownInfoResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;

import java.util.List;

/**
 */
public class HometownInfoFragment extends BaseFragment {

  @InjectView(R.id.alv_hometowns)
  public AdvancedListView mAlvHometowns;

  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    detachSelf();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_hometown_info, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    requestHometownInfo();
  }

  private void requestHometownInfo() {
    U.request("get_hometown", new OnResponse2<HometownInfoResponse>() {
      @Override
      public void onResponse(HometownInfoResponse response) {
        if (RESTRequester.responseOk(response)) {
          mAlvHometowns.setAdapter(new HometownInfoAdapter(response.object.lists));
        } else {
          detachSelf();
        }
      }

      @Override
      public void onResponseError(Throwable e) {
        detachSelf();
      }
    }, HometownInfoResponse.class, null, null);
  }

  private class HometownInfoAdapter extends BaseAdapter {

    private List<HometownInfoResponse.HometownInfo> mInfo;

    public HometownInfoAdapter(List<HometownInfoResponse.HometownInfo> info) {
      mInfo = info;
    }

    @Override
    public int getCount() {
      return mInfo.size();
    }

    @Override
    public HometownInfoResponse.HometownInfo getItem(int position) {
      return mInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_circle_region, parent, false);
      }
      HometownInfoResponse.HometownInfo info = getItem(position);
      ((TextView) convertView.findViewById(R.id.tv_circle_name))
          .setText(info.name);
      ((TextView) convertView.findViewById(R.id.tv_circle_info))
          .setText(info.info);
      return convertView;
    }
  }

  @Override
  public boolean onBackPressed() {
    return detachSelf();
  }
}
