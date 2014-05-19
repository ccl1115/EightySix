package com.utree.eightysix.test.storage;

import android.test.AndroidTestCase;
import com.utree.eightysix.storage.Storage;
import com.utree.eightysix.storage.cloud.OSSImpl;

/**
 * Test cases for OSSImpl
 *
 * @see com.utree.eightysix.storage.cloud.OSSImpl
 */
public class OSSImplTestCase extends AndroidTestCase {

    private static final String ACCESS_KEY_ID = "test access key id";
    private static final String ACCESS_KEY_SECRET = "test access key secret";

    private Storage mStorage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mStorage = new OSSImpl(ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    public void testCreateBucket() throws Exception {

        // TODO implementation
    }

    public void testDeleteBucket() throws Exception {

        // TODO implementation
    }

    public void testGet() throws Exception {

        // TODO implementation
    }

    public void testPut() throws Exception {

        // TODO implementation
    }

    public void testDelete() throws Exception {

        // TODO implementation
    }
}
