package com.utree.eightysix.app.share;

import android.content.Context;
import android.os.Bundle;
import com.utree.eightysix.R;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
public abstract class ShareDialog extends ThemedDialog {

  private String mTitle;

  public ShareDialog(Context context, String title) {
    super(context);
    mTitle = title;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(mTitle);
    setContent(R.layout.dialog_content_share);
    getViewHolder(this);
  }

  protected abstract Object getViewHolder(ShareDialog dialog);
}
