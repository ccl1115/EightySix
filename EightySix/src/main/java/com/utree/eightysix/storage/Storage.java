package com.utree.eightysix.storage;

import java.io.File;

/**
 * An interface to manipulate storage
 * <p/>
 * All methods start with letter 'a' is in async.
 */
public interface Storage {

    int ERROR_CHECKSUM = 1;
    int ERROR_GENERAL_EXCEPTION = 2;
    int ERROR_CREATE_BUCKET_FAILED = 3;
    int ERROR_GET_OBJECT_FAILED = 4;
    int ERROR_FILE_EXIST = 5;
    int ERROR_VALIDATING_FAILED = 6;
    int ERROR_REMOTE_ERROR = 7;

    String ERROR_CHECKSUM_MSG = "File object checksum failed: ";
    String ERROR_GENERAL_EXCEPTION_MSG = "Exception: ";
    String ERROR_CREATE_BUCKET_FAILED_MSG = "Create bucket failed: ";
    String ERROR_GET_OBJECT_FAILED_MSG = "Get object failed: ";
    String ERROR_FILE_EXIST_MSG = "File exist: ";
    String ERROR_VALIDATING_FAILED_MSG = "Validating failed: ";
    String ERROR_REMOTE_ERROR_MSG = "Remote error: ";


    class Result<T> {
        public int error = 0;
        public String msg = "";
        public T object;
    }

    interface OnResult {
        void onResult(Result result);
    }

    /**
     * put an object to a bucket
     *
     * @param bucket the bucket to put
     * @param path   the path
     * @param key    the object key
     * @param file   the object
     * @return the result
     */
    Result put(String bucket, String path, String key, File file);

    /**
     * put an object to a bucket in async
     *
     * @param bucket   the bucket to put
     * @param path     the path
     * @param key      the object key
     * @param file     the object
     * @param onResult the result callback
     */
    void aPut(String bucket, String path, String key, File file, OnResult onResult);

    /**
     * get an object from a bucket
     *
     * @param bucket the bucket
     * @param path   the path
     * @param key    the object key
     * @return the result
     */
    Result get(String bucket, String path, String key);

    void aGet(String bucket, String path, String key, OnResult onResult);

    Result delete(String bucket, String path, String key);

    void aDelete(String bucket, String path, String key, OnResult onResult);

    Result createBucket(String bucket);

    void aCreateBucket(String bucket, OnResult onResult);

    Result deleteBucket(String bucket);

    void aDeleteBucket(String bucket, OnResult onResult);
}
