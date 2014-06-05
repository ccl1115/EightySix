package com.utree.eightysix.app.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.FileUtils;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import com.utree.eightysix.widget.PostEditText;
import com.utree.eightysix.widget.RoundedButton;
import java.io.File;
import java.util.Random;

/**
 */
@Layout(R.layout.activity_post)
@TopTitle(R.string.post)
public class PostActivity extends BaseActivity {

    private static final String FIRST_RUN_KEY = "post_activity";

    private static final int REQUEST_CODE_CAMERA = 0x1;
    private static final int REQUEST_CODE_ALBUM = 0x2;
    private static final int REQUEST_CODE_CROP = 0x4;

    @ViewId(R.id.et_post_content)
    public PostEditText mPostEditText;

    @ViewId(R.id.tv_bottom)
    @OnClick
    public TextView mTvBottom;

    @ViewId(R.id.iv_camera)
    @OnClick
    public ImageView mIvCamera;

    @ViewId(R.id.iv_shuffle)
    @OnClick
    public ImageView mIvShuffle;

    @ViewId(R.id.rb_post_tip)
    public RoundedButton mPostTip;

    private Dialog mCameraDialog;

    private Dialog mDescriptionDialog;

    private Dialog mConfirmQuitDialog;

    private File mOutputFile;

    private boolean mIsOpened;

    private boolean mToastShown;

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.iv_camera:
                mCameraDialog.show();
                break;
            case R.id.iv_shuffle:
                if (Env.firstRun(FIRST_RUN_KEY) && !mToastShown) {
                    showToast(getString(R.string.shuffle_bg_color));
                    mToastShown = true;
                }
                mPostEditText.setBackgroundColor(new Random().nextInt());
                break;
            case R.id.tv_bottom:
                mDescriptionDialog.show();
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View activityRootView = findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.

                    if (!mIsOpened) {
                        mTvBottom.setVisibility(View.GONE);
                    }
                    mIsOpened = true;
                } else if (mIsOpened) {
                    mTvBottom.setVisibility(View.VISIBLE);
                    mIsOpened = false;
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.add_photo).setItems(new String[]{
                getString(R.string.use_camera),
                getString(R.string.select_album)
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        startCamera();
                        break;
                    case 1:
                        startAlbum();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        });

        mCameraDialog = builder.create();

        builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.who_will_see_this_secret).setMessage(R.string.post_description)
                .setPositiveButton(getString(R.string.got_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == Dialog.BUTTON_POSITIVE) {
                            dialog.dismiss();
                        }
                    }
                });

        mDescriptionDialog = builder.create();

        builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.quit_confirm))
                .setPositiveButton(getString(R.string.resume_editing), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        mConfirmQuitDialog = builder.create();

        if (Env.firstRun(FIRST_RUN_KEY)) {
            mDescriptionDialog.show();
        }

        mPostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mPostTip.setVisibility(View.VISIBLE);
                } else {
                    mPostTip.setVisibility(View.INVISIBLE);
                }

                if (InputValidator.post(s)) {
                    showToast(R.string.post_over_length);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Env.setFirstRun(FIRST_RUN_KEY, false);
    }

    @Override
    public void onBackPressed() {

        if (TextUtils.isEmpty(mPostEditText.getText())) {
            super.onBackPressed();
        } else {
            mConfirmQuitDialog.show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) return;

        switch (requestCode) {
            case REQUEST_CODE_ALBUM:
                if (data != null) {
                    Uri uri = data.getData();

                    Cursor cursor = getContentResolver()
                            .query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

                    if (cursor.moveToFirst()) {
                        String p = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        mOutputFile = new File(p);
                        startCrop();
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (mOutputFile != null) {
                    startCrop();
                }
                break;
            case REQUEST_CODE_CROP:
                if (data != null) {
                    Uri uri = data.getData();

                    Cursor cursor = getContentResolver()
                            .query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

                    if (cursor.moveToFirst()) {
                        String p = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        mPostEditText.setBackgroundDrawable(new BitmapDrawable(getResources(),
                                BitmapFactory.decodeFile(new File(p).getAbsolutePath())));
                    }
                }
                break;
            default:
                break;
        }
    }

    private void startCamera() {
        try {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mOutputFile = FileUtils.createTmpFile("camera_output");
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutputFile));
            startActivityForResult(i, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            U.getAnalyser().reportException(this, e);
        }
    }

    private void startAlbum() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, REQUEST_CODE_ALBUM);
        } catch (Exception e) {
            U.getAnalyser().reportException(this, e);
        }
    }

    private void startCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(Uri.fromFile(mOutputFile), "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        } catch (Exception e) {
            U.getAnalyser().reportException(this, e);
        }
    }
}