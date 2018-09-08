package com.googlecode.luceneappengine;

import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import static com.googlecode.objectify.ObjectifyService.ofy;



public class LuceneAppEngineBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ObjectifyService.init();
        ObjectifyService.register(com.googlecode.luceneappengine.GaeLock.class);
        ObjectifyService.register(com.googlecode.luceneappengine.LuceneIndex.class);
        ObjectifyService.register(com.googlecode.luceneappengine.Segment.class);
        ObjectifyService.register(com.googlecode.luceneappengine.SegmentHunk.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
