LuceneAppEngine (aka LAE) [![Build Status](https://travis-ci.org/UltimaPhoenix/luceneappengine.svg?branch=master)](https://travis-ci.org/UltimaPhoenix/luceneappengine) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.googlecode.luceneappengine/luceneappengine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.googlecode.luceneappengine/luceneappengine) [![Javadocs](https://www.javadoc.io/badge/com.googlecode.luceneappengine/luceneappengine.svg?color=blue)](https://www.javadoc.io/doc/com.googlecode.luceneappengine/luceneappengine) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/0066683d0eeb4dfa82e0d387c46a9a2d)](https://www.codacy.com/app/UltimaPhoenix/luceneappengine?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=UltimaPhoenix/luceneappengine&amp;utm_campaign=Badge_Grade)
===============

This project provides a directory useful to build Lucene and Google App Engine powered applications.

![https://lucene.apache.org/images/lucene_logo_green_300.png](https://lucene.apache.org/images/lucene_logo_green_300.png) ![https://www.google.com/accounts/ah/appengine.jpg](https://www.google.com/accounts/ah/appengine.jpg)

Live Demo:
 * See it in action ([live demo site](https://bigtable-lucene.appspot.com)) or check the [source code](https://github.com/UltimaPhoenix/lucene-appengine-examples)

Main Features:
  * LAE 4.1.x compatible with Lucene 7.1.x, 7.2.x, 7.3.x
  * LAE 4.0.x compatible with Lucene 7.0.x (**needs Java 8**, I will not say it anymore)
  * LAE 3.4.x compatible with Lucene 5.5.x
  * LAE 3.3.x compatible with Lucene 5.4.x
  * LAE 3.2.x compatible with Lucene 5.3.x
  * LAE 3.1.x compatible with Lucene 5.2.x, 5.1.x
  * LAE 3.0.x compatible with Lucene 5.0.x
  * LAE 2.2.x compatible with Lucene 4.10.x, 4.9.x (**needs Java 7**, I will not say it anymore)
  * LAE 2.1.x compatible with Lucene 4.9.x (**needs Java 7**)
  * LAE 2.0.x compatible with Lucene 4.8.x (**needs Java 7**)
  * LAE 1.2.x compatible with Lucene 4.7.x
  * LAE 1.1.x compatible with Lucene 4.6.x
  * LAE 1.0.x compatible with Lucene 4.0.x, 4.1.x, 4.2.x, 4.3.x, 4.4.x, 4.5.x
  * Objecitfy updated to 5.1.x
  * Storage in Google App Engine
  * Supported operations: Add, Remove, Update, Index, Deindex, everything?! (I need you to check)
  * Multiple indexes in the same application
  * No more ([the wrong](http://stackoverflow.com/questions/9176993/disable-concurrentmergescheduler-in-lucene-3-5-0/12164826#12164826)) [RAMDirectory](http://lucene.apache.org/core/3_6_1/api/all/org/apache/lucene/store/RAMDirectory.html) for Google App Engine powered applications
  * No more index size limit of 1MB
  * Yes! It works, see it in action ([live demo site](https://bigtable-lucene.appspot.com)) with  [source code](https://github.com/UltimaPhoenix/lucene-appengine-examples)
  * Open your mind to new Google App Engine applications powered by Lucene
  * For high performance applications use [google app engine task queue](https://developers.google.com/appengine/docs/java/taskqueue)
  * [Memcache](https://developers.google.com/appengine/docs/java/memcache/) supported and integrated for all versions
  * Distributed on [maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.luceneappengine%22)
  * Easier to configure than the [previous LAE version](https://code.google.com/p/lucene-appengine)

Coming Soon:
  * 
  * Performance improvements:
  * asynch indexing
  * buffer management

## News
  * 14-04-2018 tested LAE 4.1.0 with LUCENE 7.3.0
  * 21-01-2018 tested LAE 4.1.0 with LUCENE 7.2.1
  * 21-01-2018 completed development of LAE 4.1.0 and LUCENE 7.2.0
  * 13-11-2017 completed development of LAE 4.0.0 and LUCENE 7.1.0
  * 28-06-2017 Today Google completed the long standing [feature request for Servlet 3 and Java 8](https://issuetracker.google.com/issues/35887151) and I'm happy to start to adapt Lucene 6 to the new [beta standard environment Java 8 powered](https://cloudplatform.googleblog.com/2017/06/Google-App-Engine-standard-now-supports-Java-8.html)
  * 23-07-2016 Unfortunately Lucene 6 need Java 8 and AppEngine standard environment only supports Java 7
  * 23-04-2016 Released New LAE-3.4.0 compatible with Lucene 5.5.x
  * 27-12-2015 Released New LAE-3.3.0 compatible with Lucene 5.4.x
  * 01-11-2015 Released New LAE-3.2.0 compatible with Lucene 5.3.x
  * 27-09-2015 Released New LAE-3.1.1 compatible with Lucene 5.2.x, 5.1.x 
  * 13-09-2015 Released New LAE-3.1.0 (broken version: can only read from index)
  * 05-07-2015 Wiki officially moved to GitHub
  * 05-07-2015 Released New LAE-3.0.0 compatible with Lucene 5.0.0 (this is a major release with breaking changes)
  * 17-09-2014 Released New LAE-2.2.0 compatible with Lucene 4.10.x and Lucene 4.9.x 
  * 10-08-2014 Released New LAE-2.1.0 compatible with Lucene 4.9.x 
  * 23-07-2014 Released New LAE-2.0.1 **bugfix** and **performance improvements** release, **all other versions** are now **deprecated**
  * 20-05-2014 Released New LAE-2.0.0 compatible with Lucene 4.8.x
  * 05-04-2014 Released New LAE-1.2.0 compatible with Lucene 4.7.x
  * 05-04-2014 Released New LAE-1.1.0 compatible only with Lucene 4.6.x
  * 03-04-2014 Migration of source code completed on [GitHub](https://github.com/UltimaPhoenix/luceneappengine)
  * 03-04-2014 Released New LAE-1.0.3 on [Maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.luceneappengine%22)
  * 03-04-2014 Discontinued Objecfity 3 support, if you need it refer to the [old site](https://code.google.com/p/lucene-appengine)
  * 03-04-2014 Discontinued Lucene 3.6.x support, if you need it refer to the [old site](https://code.google.com/p/lucene-appengine)


# Usage and Config
Check the [wiki page](../../wiki/Usage-and-Configuration). Or start from the [live demo source](https://github.com/UltimaPhoenix/lucene-appengine-examples)

## Development
If you like this project, you can [choose a kind of donation here](../../wiki/Donate) or
[![](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=LJXCLX64T7Z74&lc=GB&item_name=Lucene%20App%20Engine%20Project&item_number=LuceneAppEngine&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted) as much as you want. Thanks for your support!
