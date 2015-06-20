/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.utree.eightysix.U;
import com.utree.eightysix.widget.ThemedDialog;

/**
 */
public class ProfileFillDialog extends ThemedDialog {

  public ProfileFillDialog(Context context) {
    super(context);
    setTitle("先去完善资料吧");

    TextView textView = new TextView(context);

    int padding = U.dp2px(16);
    textView.setText("设置昵称、头像后，大家才能认识你，才能同意别人的请求哦");
    textView.setPadding(padding, padding, padding, padding);

    setContent(textView);

    setPositive("去设置", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ProfileFillActivity.start(getContext(), false);
        dismiss();
      }
    });

    setRbNegative("下次再说", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
  }
}
