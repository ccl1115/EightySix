package com.utree.eightysix.app.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.FileUtils;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;
import com.utree.eightysix.widget.PostEditText;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TopBar;
import java.io.File;
import java.util.Random;

/**
 */
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

    @ViewId(R.id.iv_post_bg)
    public ImageView mIvPostBg;

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
                int color = new Random().nextInt();
                mIvPostBg.setImageDrawable(new ColorDrawable(color));
                mPostEditText.setTextColor((color & 0x00FFFFFF) > 0x00888888 ? Color.BLACK : Color.WHITE);
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

        setContentView(new PostActivityLayout(this));

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
                        if (!startCamera()) {
                            showToast(R.string.error_start_camera);
                        }
                        break;
                    case 1:
                        if (!startAlbum()) {
                            showToast(R.string.error_start_album);
                        }
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

                if (!InputValidator.post(s) && count > 0) {
                    showToast(R.string.post_over_length);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
            @Override
            public String getTitle(int position) {
                return getString(R.string.post);
            }

            @Override
            public Drawable getIcon(int position) {
                return null;
            }

            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mPostEditText.getWindowToken(), 0);

                    finish();
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
    }

    @Override
    protected void onDestroy() {
        Env.setFirstRun(FIRST_RUN_KEY, false);

        super.onDestroy();
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
                        if (!startCrop()) {
                            setBgImage(p);
                        }
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (mOutputFile != null) {
                    if (!startCrop()) {
                        setBgImage(mOutputFile.getAbsolutePath());
                    }
                }
                break;
            case REQUEST_CODE_CROP:
                if (data != null) {
                    Uri uri = data.getData();

                    Cursor cursor = getContentResolver()
                            .query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

                    if (cursor.moveToFirst()) {
                        String p = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        setBgImage(p);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setBgImage(String p) {
        Bitmap bitmap = BitmapFactory.decodeFile(new File(p).getAbsolutePath());
        mPostEditText.setTextColor(Color.WHITE);
        mPostEditText.setShadowLayer(2, 0, 0, Color.WHITE);
        mIvPostBg.setImageBitmap(bitmap);
        mIvPostBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private boolean startCamera() {
        try {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mOutputFile = FileUtils.createTmpFile("camera_output");
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutputFile));
            startActivityForResult(i, REQUEST_CODE_CAMERA);
            return true;
        } catch (Exception e) {
            U.getAnalyser().reportException(this, e);
            return false;
        }
    }

    private boolean startAlbum() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, REQUEST_CODE_ALBUM);
            return true;
        } catch (Exception e) {
            U.getAnalyser().reportException(this, e);
            return false;
        }
    }

    private boolean startCrop() {
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
            return true;
        } catch (Exception e) {
            U.getAnalyser().reportException(this, e);
            return false;
        }
    }
}