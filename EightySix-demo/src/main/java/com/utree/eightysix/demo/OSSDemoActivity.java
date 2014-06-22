package com.utree.eightysix.demo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.storage.Storage;
import de.akquinet.android.androlog.Log;
import java.io.File;

/**
 */
public class OSSDemoActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICKFILE = 0x001;

    @InjectView(R.id.bucket)
    public EditText mBucket;

    @InjectView(R.id.path)
    public EditText mPath;

    @InjectView(R.id.file_name)
    public EditText mFile;

    @InjectView(R.id.button_choose_file)
    public Button mChooseFile;

    @InjectView(R.id.button_create_bucket)
    public Button mCreateBucket;

    @InjectView(R.id.button_delete_bucket)
    public Button mDeleteBucket;

    @InjectView(R.id.button_delete_file)
    public Button mDeleteFile;

    @InjectView(R.id.button_upload_file)
    public Button mUploadFile;


    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oss_demo);

        setTopTitle(getString(R.string.title_oss_demo_activity));
    }

    @OnClick(R.id.button_upload_file)
    public void onBtnUploadFile() {
        U.getCloudStorage().aPut(mBucket.getText().toString(),
                mPath.getText().toString(),
                mFileName,
                new File(mFile.getText().toString()),
                new Storage.OnResult() {
                    @Override
                    public void onResult(Storage.Result result) {
                        Log.d(OSSDemoActivity.this, "  " + result.msg);
                    }
                }
        );
    }

    @OnClick(R.id.button_delete_bucket)
    public void onBtnDeleteBucketClicked() {
        U.getCloudStorage().aDeleteBucket(mBucket.getText().toString(), new Storage.OnResult() {
            @Override
            public void onResult(Storage.Result result) {
                Log.d(this, result.msg);
            }
        });
    }

    @OnClick(R.id.button_create_bucket)
    public void onBtnCreateBucketClicked() {
        U.getCloudStorage().aCreateBucket(mBucket.getText().toString(), new Storage.OnResult() {
            @Override
            public void onResult(Storage.Result result) {
                Log.d(this, result.msg);
            }
        });
    }

    @OnClick(R.id.button_delete_file)
    public void onBtnDeleteFileClicked() {
        U.getCloudStorage().aDelete(mBucket.getText().toString(), mPath.getText().toString(), mFile.getText().toString(),
                new Storage.OnResult() {
                    @Override
                    public void onResult(Storage.Result result) {
                        Log.d(this, result.msg);
                    }
                }
        );
    }

    @OnClick(R.id.button_choose_file)
    public void onBtnChooseFileClicked() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
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
                                mFile.setText(path);
                                mFileName = name;
                            } else {
                                Toast.makeText(this, "Failed to upload file", Toast.LENGTH_LONG).show();
                            }
                            cursor.close();
                        }

                    }
                }
            }

        }
    }
}