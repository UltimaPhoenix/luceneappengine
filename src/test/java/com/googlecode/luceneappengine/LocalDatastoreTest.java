package com.googlecode.luceneappengine;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public abstract class LocalDatastoreTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


    @Before
    public void setUp() {
        this.helper.setUp();
    }

    @After
    public void tearDown() {
        this.helper.tearDown();
    }
}
