package com.utree.eightysix.app.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.MD5Util;
import com.utree.eightysix.widget.ThemedDialog;
import java.io.File;

/**
 * @author simon
 */
public class UpgradeDialog extends ThemedDialog {
  private Sync.Upgrade mUpgrade;
  private RequestHandle mRequestHandle;

  public UpgradeDialog(Context context, Sync.Upgrade upgrade) {
    super(context);
    mUpgrade = upgrade;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle(R.string.new_version_to_upgrade);

    LayoutInflater.from(getContext()).inflate(R.layout.activity_upgrade, mFlContent, true);
    ViewHolder viewHolder = new ViewHolder(mFlContent);

    viewHolder.mTvInfo.setText(mUpgrade.info);
    viewHolder.mTvVersion.setText(U.gfs(R.string.new_version, mUpgrade.version));

    setPositive(R.string.download_upgrade, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        UpgradeService.start(getContext(), mUpgrade);
      }
    });

    if (mUpgrade.force == 1) {
      setCancelable(false);
      setCanceledOnTouchOutside(false);
      setRbNegative(R.string.exit_app, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          android.os.Process.killProcess(android.os.Process.myPid());
        }
      });
    } else {
      setCancelable(true);
      setCanceledOnTouchOutside(false);
      setRbNegative(R.string.later, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          if (mRequestHandle != null) {
            mRequestHandle.cancel(true);
          }
        }
      });
    }

    setOnDismissListener(new OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialog) {
        Env.setUpgradeCanceledTimestamp();
      }
    });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if (mUpgrade.force == 1) {
      U.showToast(getContext().getString(R.string.force_update_tip));
    }
  }

  public static class ViewHolder {

    @InjectView (R.id.tv_version)
    public TextView mTvVersion;
    @InjectView (R.id.tv_info)
    public TextView mTvInfo;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
