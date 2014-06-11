package com.utree.eightysix.demo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.aliyun.android.util.MD5Util;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import butterknife.OnClick;
import butterknife.InjectView;
import com.utree.eightysix.widget.AsyncImageView;
import de.akquinet.android.androlog.Log;
import java.io.File;

/**
 */
@Layout(R.layout.activity_demo_image_utils)
public class ImageUtilsDemoActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICKFILE = 0x1;

    @InjectView(R.id.btn_upload)
    public Button mBtnUpload;

    @InjectView(R.id.aiv_show)
    public AsyncImageView mAivShow;


    private String mFileHash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.btn_upload)
    public void onBtnUpload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pick a file"), REQUEST_CODE_PICKFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKFILE) {
            if (data != null) {
                if (BuildConfig.DEBUG) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        String[] columns = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME};

                        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                final String path = cursor.getString(cursor.getColumnIndex(columns[0]));
                                final String name = cursor.getString(cursor.getColumnIndex(columns[1]));
                                Log.d(this, path);
                                Log.d(this, name);
                                File file = new File(path);
                                mFileHash = IOUtils.fileHash(file);
                                ImageUtils.asyncUpload(file);
                            }
                            cursor.close();
                            return;
                        }

                    }
                    Toast.makeText(this, "Failed to upload file", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    @Subscribe public void onImageUploadedEvent(ImageUtils.ImageUploadedEvent event) {
        Log.d("ImageUtils", "image uploaded : " + event.getUrl());
        if (event.getHash().equals(mFileHash)) {
            final String url = event.getUrl();
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAivShow.setUrl(url);
                }
            }, 3000);
        }
    }
}