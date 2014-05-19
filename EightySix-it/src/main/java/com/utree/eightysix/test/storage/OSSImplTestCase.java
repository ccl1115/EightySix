package com.utree.eightysix.test.storage;

import android.test.AndroidTestCase;
import com.utree.eightysix.storage.Storage;
import com.utree.eightysix.storage.cloud.OSSImpl;

/**
 */
public class OSSImplTestCase extends AndroidTestCase {

    private Storage mStorage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mStorage = new OSSImpl();
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
