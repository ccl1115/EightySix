package com.utree.eightysix.demo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.storage.Storage;
import com.utree.eightysix.utils.ViewBinding;
import de.akquinet.android.androlog.Log;

import java.io.File;

/**
 */
public class OSSDemoActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PICKFILE = 0x001;

    @ViewBinding.ViewId(R.id.bucket)
    public EditText mBucket;

    @ViewBinding.ViewId(R.id.path)
    public EditText mPath;

    @ViewBinding.ViewId(R.id.button_choose_file)
    @ViewBinding.OnClick
    public Button mChooseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oss_demo);
        U.viewBinding(findViewById(R.id.content), this);

        setTitle(getString(R.string.title_oss_demo_activity));
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.button_choose_file:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(Intent.createChooser(intent, "Pick a file"), REQUEST_CODE_PICKFILE);
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
                        if (cursor.moveToFirst()) {
                            final String path = cursor.getString(cursor.getColumnIndex(columns[0]));
                            final String name = cursor.getString(cursor.getColumnIndex(columns[1]));
                            Log.d(this, path);
                            Log.d(this, name);
                            U.getCloudStorage().aPut(mBucket.getText().toString(),
                                    mBucket.getText().toString(),
                                    name,
                                    new File(path),
                                    new Storage.OnResult() {
                                        @Override
                                        public void onResult(Storage.Result result) {
                                            Log.d(OSSDemoActivity.this, "  " + result.msg);
                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(this, "Failed to upload file", Toast.LENGTH_LONG).show();
                        }


                    }
                }
            }

        }
    }
}
