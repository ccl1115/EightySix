package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.aliyun.android.util.MD5Util;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.ImageUtils;

/**
 */
public class AsyncImageView extends ImageView {

    private String mUrlHash;

    public AsyncImageView(Context context) {
        this(context, null, 0);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Subscribe public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
        if (event.getHash().equals(mUrlHash)) {
            if (event.getBitmap() != null) {
                setImageBitmap(event.getBitmap());
            }
        }
        U.getBus().unregister(this);
    }

    public void setUrl(String url) {
        if (url == null) return;

        if (url.equals(mUrlHash)) return;

        mUrlHash = MD5Util.getMD5String(url.getBytes()).toLowerCase();

        U.getBus().register(this);

        ImageUtils.asyncLoad(url, mUrlHash);
    }
}
