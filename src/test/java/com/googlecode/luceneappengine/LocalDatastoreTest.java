package com.googlecode.luceneappengine;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import org.junit.After;
import org.junit.Before;

public abstract class LocalDatastoreTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

    Closeable ofyContext;
    
    @Before
    public void setUp() {
        helper.setUp();
        ofyContext = ObjectifyService.begin();
        ofy().clear();
    }

    @After
    public void tearDown() {
    	ofyContext.close();
        helper.tearDown();
    }
}
