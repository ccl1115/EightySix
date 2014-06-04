package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import com.utree.eightysix.R;
import de.akquinet.android.androlog.Log;
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightSize + MeasureSpec.EXACTLY);
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
            setTextColor(art.getPrimaryColor());
            setShadowLayer(2, 0, 0, art.getSecondaryColor());
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);

        Bitmap bitmap = Bitmap.createBitmap(new int[] {color, color, color, color}, 2, 2, Bitmap.Config.ARGB_8888);

        if (bitmap != null) {
            ColorArt art = new ColorArt(bitmap);
            Log.d(this, String.format("Primary color: %h", art.getPrimaryColor()));
            Log.d(this, String.format("Secondary color: %h", art.getSecondaryColor()));
            Log.d(this, String.format("Background color: %h", art.getBackgroundColor()));
            Log.d(this, String.format("Detail color: %h", art.getDetailColor()));
            setTextColor(art.getPrimaryColor());
            setShadowLayer(2, 0, 0, art.getSecondaryColor());
        }
    }

}
