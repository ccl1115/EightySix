package com.utree.eightysix.demo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import com.utree.eightysix.widget.AsyncImageView;
import de.akquinet.android.androlog.Log;
import java.io.File;

/**
 */
@Layout(R.layout.activity_demo_image_utils)
public class ImageUtilsDemoActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICKFILE = 0x1;

    @ViewId(R.id.btn_upload)
    @OnClick
    public Button mBtnUpload;

    @ViewId(R.id.aiv_show)
    public AsyncImageView mAivShow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.btn_upload:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pick a file"), REQUEST_CODE_PICKFILE);
                break;
            default:
                break;
        }
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
                                ImageUtils.asyncUpload(new File(path));
                                return;
                            }
                            cursor.close();
                        }

                    }
                    Toast.makeText(this, "Failed to upload file", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    @Subscribe public void onImageUploadedEvent(ImageUtils.ImageUploadedEvent event) {
        Log.d("ImageUtils", "image uploaded : " + event.getUrl());
        mAivShow.setUrl(event.getUrl());
    }
}