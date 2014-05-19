package com.utree.eightysix.storage.cloud;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSErrorCode;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.Bucket;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.storage.Storage;
import com.utree.eightysix.storage.StorageConfig;
import de.akquinet.android.androlog.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Aliyun OSS Implementation
 */
public class OSSImpl implements Storage {

    private static final String TAG = "OSSImpl";

    private static final String ACCESS_KEY_ID = "tUDRZZW0ErGfob0D";

    private static final String ACCESS_KEY_SECRET = "JY9qIePwsObqq6MWzh2TXLiSylP55F";

    private OSSClient mOSSClient;

    private final PathValidator mPathValidator = new PathValidator();

    public OSSImpl() {
        mOSSClient = new OSSClient(ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    @Override
    public Result<OSSObject> put(String bucket, String path, String key, File file) {
        Result<OSSObject> result = new Result<OSSObject>();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            FileInputStream input = new FileInputStream(file);
            metadata.setContentLength(file.length());
            String md5 = DigestUtils.md5Hex(input);
            PutObjectResult putObjectResult = mOSSClient.putObject(bucket, path + key, input, metadata);

            if (!putObjectResult.getETag().equals(md5)) {
                result.error = ERROR_CHECKSUM;
                result.msg = ERROR_CHECKSUM_MSG;
            }
        } catch (FileNotFoundException e) {
            result.error = ERROR_GENERAL_EXCEPTION;
            result.msg = ERROR_GENERAL_EXCEPTION_MSG;
        } catch (IOException e) {
            result.error = ERROR_GENERAL_EXCEPTION;
            result.msg = ERROR_GENERAL_EXCEPTION_MSG;
        }
        return result;
    }

    @Override
    public void aPut(final String bucket, final String path, final String key, final File file, final OnResult onResult) {
        new AsyncTask<Void, Void, Result<OSSObject>>() {

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

    private Result doDelete(String bucket, String path, String key, Result result) {
        try {
            mOSSClient.deleteObject(bucket, path + key);
        } catch (OSSException e) {
            handleException(e, "delete failed");
            result.error = ERROR_REMOTE_ERROR;
            result.msg = ERROR_REMOTE_ERROR_MSG + e.getMessage();
        }
        return result;
    }

    @Override
    public void aDelete(final String bucket, final String path, final String key, final OnResult onResult) {
        new AsyncTask<Void, Void, Result>() {
            final Result<Void> result = new Result<Void>();

            @Override
            protected void onPreExecute() {
                if (!preDelete(bucket, path, key, result)) {
                    cancel(true);
                    if (onResult != null) {
                        onResult.onResult(result);
                    }
                }
            }

            @Override
            protected Result doInBackground(Void... params) {
                return doDelete(bucket, path, key, result);
            }

            @Override
            protected void onPostExecute(Result result) {
                if (onResult != null) {
                    onResult.onResult(result);
                }
            }
        }.execute();
    }


    @Override
    public Result createBucket(String bucket) {
        final Result result = new Result();

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

    private void doCreateBucket(String bucket, Result result) {
        Bucket b = mOSSClient.createBucket(bucket);
        if (b == null) {
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

    private void handleException(OSSException e, String action) {
        Log.d(TAG, action + ": " + e.getErrorCode());
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    private static class PathValidator {

        private static final int MAX_PATH_KEY_LENGTH = 1023;

        private final Pattern mBucketPattern = Pattern.compile("[0-9a-z][0-9a-z_]{2,62}");
        private final Pattern mPathPattern = Pattern.compile("[0-9a-zA-Z][0-9a-zA-Z_\\-\\.]{0,253}");


        boolean validateBucketName(String name) {
            return mBucketPattern.matcher(name).matches();
        }

        boolean validatePathKey(String path, String key) {
            if (!TextUtils.isEmpty(key)) {
                if (!mPathPattern.matcher(key).matches()) {
                    return false;
                }
            } else {
                return false;
            }

            if (!TextUtils.isEmpty(path)) {
                if (path.endsWith(File.separator)) {
                    path = TextUtils.substring(path, 0, path.length() - 2);
                }
                String[] ps = path.split(File.separator);
                for (String p : ps) {
                    if (!mPathPattern.matcher(p).matches()) {
                        return false;
                    }
                }
            }

            return true;
        }

        boolean validate(String bucket, String path, String key) {
            return ((bucket + path + key).length() < MAX_PATH_KEY_LENGTH) && validateBucketName(bucket) && validatePathKey(path, key);
        }
    }

}
