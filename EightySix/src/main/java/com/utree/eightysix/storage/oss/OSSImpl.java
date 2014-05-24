package com.utree.eightysix.storage.oss;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.aliyun.android.oss.OSSClient;
import com.aliyun.android.oss.OSSException;
import com.aliyun.android.oss.model.Bucket;
import com.aliyun.android.oss.model.OSSObject;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.storage.Storage;
import de.akquinet.android.androlog.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * 阿里云存储服务实现
 */
public class OSSImpl implements Storage {

    private static final String TAG = "OSSImpl";

    private static final String ACCESS_KEY_ID = "tUDRZZW0ErGfob0D";
    private static final String ACCESS_KEY_SECRET = "THPrVUnQiAv2wgdp2rSd8MHdUJyMW9";

    private final PathValidator mPathValidator = new PathValidator();
    private OSSClient mOSSClient;

    public OSSImpl() {
        mOSSClient = new OSSClient();
        mOSSClient.setAccessId(ACCESS_KEY_ID);
        mOSSClient.setAccessKey(ACCESS_KEY_SECRET);
    }

    public OSSImpl(String accessKeyId, String accessKeySecret) {
        mOSSClient = new OSSClient();
        mOSSClient.setAccessId(accessKeyId);
        mOSSClient.setAccessKey(accessKeySecret);
    }

    @Override
    public Result<OSSObject> put(String bucket, String path, String key, File file) {
        final Result<OSSObject> result = new Result<OSSObject>();
        if (prePut(bucket, path, key, file, result)) {
            doPut(bucket, path, key, file, result);
        }
        return result;
    }

    private boolean prePut(String bucket, String path, String key, File file, Result result) {
        return mPathValidator.validate(bucket, path, key) && file.exists();
    }

    private void doPut(String bucket, String path, String key, File file, Result result) {
        try {
            mOSSClient.uploadObject(bucket, path + key, file.getAbsolutePath());
        } catch (OSSException e) {
            result.msg = e.getErrorCode();
        }
    }

    @Override
    public void aPut(final String bucket, final String path, final String key, final File file, final OnResult onResult) {
        new AsyncTask<Void, Void, Result<OSSObject>>() {

            final Result<OSSObject> mResult = new Result<OSSObject>();

            @Override
            protected void onPreExecute() {
                if (!prePut(bucket, path, key, file, mResult)) {
                    cancel(true);
                    if (onResult != null) {
                        onResult.onResult(mResult);
                    }
                }
            }

            @Override
            protected Result<OSSObject> doInBackground(Void... params) {
                return put(bucket, path, key, file);
            }

            @Override
            protected void onPostExecute(Result<OSSObject> result) {
                if (onResult != null) {
                    onResult.onResult(result);
                }
            }
        }.execute();
    }

    @Override
    public Result<OSSObject> get(String bucket, String path, String key) {
        Result<OSSObject> result = new Result<OSSObject>();

        if (preGet(bucket, path, key, result)) {
            doGet(bucket, path, key, result);
        }

        return result;
    }

    private boolean preGet(String bucket, String path, String key, Result<OSSObject> result) {
        boolean bool = mPathValidator.validate(bucket, path, key);
        if (!bool) {
            result.error = ERROR_VALIDATING_FAILED;
            result.msg = ERROR_VALIDATING_FAILED_MSG;
        }
        return bool;
    }

    private void doGet(String bucket, String path, String key, Result<OSSObject> result) {
        OSSObject object = null;
        try {
            object = mOSSClient.getObject(bucket, path + key);
        } catch (OSSException e) {
            handleException(e, "get failed");
        }

        if (object != null) {
            result.object = object;
        } else {
            result.error = ERROR_GET_OBJECT_FAILED;
            result.msg = ERROR_GET_OBJECT_FAILED_MSG;
        }
    }

    @Override
    public void aGet(final String bucket, final String path, final String key, final OnResult onResult) {
        new AsyncTask<Void, Void, Void>() {

            private final Result<OSSObject> result = new Result<OSSObject>();

            @Override
            protected void onPreExecute() {
                if (!preGet(bucket, path, key, result)) {
                    cancel(true);
                    if (onResult != null) {
                        onResult.onResult(result);
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                doGet(bucket, path, key, result);
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                if (onResult != null) {
                    onResult.onResult(result);
                }
            }
        }.execute();
    }

    @Override
    public Result delete(String bucket, String path, String key) {
        final Result<Void> result = new Result<Void>();
        if (preDelete(bucket, path, key, result)) {
            doDelete(bucket, path, key, result);
        }
        return result;
    }

    private boolean preDelete(String bucket, String path, String key, Result result) {
        boolean bool = mPathValidator.validate(bucket, path, key);
        if (!bool) {
            result.error = ERROR_VALIDATING_FAILED;
            result.msg = ERROR_VALIDATING_FAILED_MSG;
        }
        return bool;
    }

    private void doDelete(String bucket, String path, String key, Result result) {
        try {
            mOSSClient.deleteObject(bucket, path + key);
        } catch (OSSException e) {
            handleException(e, "delete failed");
            result.error = ERROR_REMOTE_ERROR;
            result.msg = ERROR_REMOTE_ERROR_MSG + e.getMessage();
        }
    }

    @Override
    public void aDelete(final String bucket, final String path, final String key, final OnResult onResult) {
        new AsyncTask<Void, Void, Void>() {
            final Result<Void> mResult = new Result<Void>();

            @Override
            protected void onPreExecute() {
                if (!preDelete(bucket, path, key, mResult)) {
                    cancel(true);
                    if (onResult != null) {
                        onResult.onResult(mResult);
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                doDelete(bucket, path, key, mResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                if (onResult != null) {
                    onResult.onResult(mResult);
                }
            }
        }.execute();
    }


    @Override
    public Result createBucket(String bucket) {
        final Result<Bucket> result = new Result<Bucket>();

        if (preCreateBucket(bucket, result)) {
            doCreateBucket(bucket, result);
        }

        return result;
    }

    private boolean preCreateBucket(String bucket, Result result) {
        boolean bool = mPathValidator.validateBucketName(bucket);
        if (!bool) {
            result.error = ERROR_VALIDATING_FAILED;
            result.msg = ERROR_VALIDATING_FAILED_MSG;
        }
        return bool;
    }

    private void doCreateBucket(String bucket, Result<Bucket> result) {
        if (!mOSSClient.createBucket(bucket)) {
            result.error = ERROR_CREATE_BUCKET_FAILED;
            result.msg = ERROR_CREATE_BUCKET_FAILED_MSG + bucket;
        }
    }

    @Override
    public void aCreateBucket(final String bucket, final OnResult onResult) {
        new AsyncTask<Void, Void, Void>() {

            private final Result mResult = new Result();

            @Override
            protected void onPreExecute() {
                if (!preCreateBucket(bucket, mResult)) {
                    cancel(true);
                    if (onResult != null) {
                        onResult.onResult(mResult);
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                doCreateBucket(bucket, mResult);
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                if (onResult != null) {
                    onResult.onResult(mResult);
                }
            }
        }.execute();
    }

    @Override
    public Result deleteBucket(String bucket) {
        final Result result = new Result();
        if (preDeleteBucket(bucket, result)) {
            doDeleteBucket(bucket, result);
        }
        return result;
    }

    private boolean preDeleteBucket(String bucket, Result result) {
        boolean bool = mPathValidator.validateBucketName(bucket);
        if (!bool) {
            result.error = ERROR_VALIDATING_FAILED;
            result.msg = ERROR_VALIDATING_FAILED_MSG;
        }
        return bool;
    }

    private void doDeleteBucket(String bucket, Result result) {
        try {
            mOSSClient.deleteBucket(bucket);
        } catch (OSSException e) {
            handleException(e, "delete bucket failed");
            result.error = ERROR_REMOTE_ERROR;
            result.msg = ERROR_REMOTE_ERROR_MSG;
        }
    }

    @Override
    public void aDeleteBucket(final String bucket, final OnResult onResult) {
        new AsyncTask<Void, Void, Void>() {

            final Result mResult = new Result();

            @Override
            protected void onPreExecute() {
                if (!preDeleteBucket(bucket, mResult)) {
                    cancel(true);
                    if (onResult != null) {
                        onResult.onResult(mResult);
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                doDeleteBucket(bucket, mResult);
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                if (onResult != null) {
                    onResult.onResult(mResult);
                }
            }
        }.execute();
    }

    private void handleException(OSSException e, String action) {
        Log.d(TAG, action + ": " + e.getErrorCode());
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    /**
     * Bucket命名规范：
     * 1. 只能包含小写字母，数字和短横线
     * 2. 必须以小写字母和数字开头
     * 3. bucket的长度限制在3-63之间
     *
     * 文件夹命名规范：
     * 1. 只能包含字母，数字，中文，下划线（_）和短横线（-）,小数点（.）
     * 2. 只能以字母、数字或者中文开头
     * 3. 文件夹的长度限制在1-254之间
     * 4. Object总长度必须在1-1023之间
     */
    private static class PathValidator {

        private static final int MAX_PATH_KEY_LENGTH = 1023;
        private final Pattern mBucketPattern = Pattern.compile("[0-9a-z][0-9a-z\\-]{2,62}");

        private final Pattern mPathPattern = Pattern.compile("[0-9a-zA-Z][0-9a-zA-Z_\\-\\.]{0,253}");


        boolean validateBucketName(String name) {
            boolean matches = mBucketPattern.matcher(name).matches();
            if (!matches) {
                Log.d(this, "validate bucket name failed");
            }
            return matches;
        }

        boolean validatePathKey(String path, String key) {
            if (TextUtils.isEmpty(key) || !mPathPattern.matcher(key).matches()) {
                Log.d(this, "validate key failed");
                return false;
            }

            if (!TextUtils.isEmpty(path)) {
                if (path.endsWith(File.separator)) {
                    path = TextUtils.substring(path, 0, path.length() - 1);
                }
                String[] ps = path.split(File.separator);
                for (String p : ps) {
                    if (!mPathPattern.matcher(p).matches()) {
                        Log.d(this, "validate path failed");
                        return false;
                    }
                }
            }

            return true;
        }

        boolean validate(String bucket, String path, String key) {
            return ((bucket + path + key).length() < MAX_PATH_KEY_LENGTH) &&
                    validateBucketName(bucket) && validatePathKey(path, key);
        }
    }

}
