package com.googlecode.luceneappengine;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;

public abstract class LocalDatastoreTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        ofy().clear();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
}
