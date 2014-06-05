package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import com.utree.eightysix.R;
import org.michaelevans.colorart.library.ColorArt;

/**
 */
public class PostEditText extends EditText {
    public PostEditText(Context context) {
        this(context, null);
    }

    public PostEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.postEditTextStyle);
    }

    public PostEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);

        Bitmap bitmap = null;
        if (background instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) background).getBitmap();
        }

        if (bitmap != null) {
            ColorArt art = new ColorArt(bitmap);
            setTextColor(art.getDetailColor());
            setShadowLayer(2, 0, 1, art.getBackgroundColor());
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);

        Bitmap bitmap = Bitmap.createBitmap(new int[] {color, color, color, color}, 2, 2, Bitmap.Config.ARGB_8888);

        if (bitmap != null) {
            ColorArt art = new ColorArt(bitmap);
            setTextColor(art.getPrimaryColor());
            setShadowLayer(2, 0, 1, art.getDetailColor());
        }
    }

}
