package com.utree.eightysix.app.share;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.utree.eightysix.R;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
public abstract class ShareDialog extends ThemedDialog {
  public ShareDialog(Context context) {
    super(context);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("分享给厂里的朋友");
    setContent(R.layout.dialog_content_share);
    getViewHolder(this);

  }

  protected abstract Object getViewHolder(ShareDialog dialog);
}
