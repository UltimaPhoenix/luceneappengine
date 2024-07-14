LuceneAppEngine (aka LAE) [![Build Status](https://travis-ci.org/UltimaPhoenix/luceneappengine.svg?branch=master)](https://travis-ci.org/UltimaPhoenix/luceneappengine) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.googlecode.luceneappengine/luceneappengine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.googlecode.luceneappengine/luceneappengine) [![Javadocs](https://www.javadoc.io/badge/com.googlecode.luceneappengine/luceneappengine.svg?color=blue)](https://www.javadoc.io/doc/com.googlecode.luceneappengine/luceneappengine) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/0066683d0eeb4dfa82e0d387c46a9a2d)](https://www.codacy.com/app/UltimaPhoenix/luceneappengine?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=UltimaPhoenix/luceneappengine&amp;utm_campaign=Badge_Grade)
===============

This project provides a directory useful to build Lucene and Google App Engine powered applications.

![https://lucene.apache.org/theme/images/lucene/lucene_logo_green_300.png](https://lucene.apache.org/theme/images/lucene/lucene_logo_green_300.png) ![https://www.google.com/accounts/ah/appengine.jpg](https://www.google.com/accounts/ah/appengine.jpg)

Live Demo:
 * See it in action ([live demo site](https://lae-firestore.appspot.com)) or check the [source code](https://github.com/UltimaPhoenix/lucene-appengine-examples)

Main Features:
  * LAE 5.0.x compatible with Lucene 9.0.x
  * Storage in google cloud firestore
  * Supported operations: Add, Remove, Update, Index, Deindex, everything?! (I need you to check)
  * Multiple indexes in the same application
  * No more index size limit of 1MB
  * Yes! It works, see it in action ([live demo site](https://lae-firestore.appspot.com)) with  [source code](https://github.com/UltimaPhoenix/lucene-appengine-examples)
  * Open your mind to new Google App Engine applications powered by Lucene
  * For high performance applications use [google app engine task queue](https://developers.google.com/appengine/docs/java/taskqueue)

Deprecated features existing LAE < 5.x.x (no longer supported):
  * LAE 4.5.x compatible with Lucene 8.6.x, 8.7.x, 8.8.x, 8.9.x, 8.10.x
  * LAE 4.4.x compatible with Lucene 8.2.x, 8.3.x, 8.4.x, 8.5.x
  * LAE 4.3.x compatible with Lucene 8.1.x
  * LAE 4.2.x compatible with Lucene 7.3.x, 7.4.x, 7.5.x, 7.6.x, 7.7.x, 8.0.x
  * LAE 4.1.x compatible with Lucene 7.1.x, 7.2.x
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
  * Yes! It works, see it in action ([live demo site (no longer available)](https://bigtable-lucene.appspot.com)) with  [source code](https://github.com/UltimaPhoenix/lucene-appengine-examples)
  * Open your mind to new Google App Engine applications powered by Lucene
  * For high performance applications use [google app engine task queue](https://developers.google.com/appengine/docs/java/taskqueue)
  * [Memcache](https://developers.google.com/appengine/docs/java/memcache/) supported and integrated for all versions
  * Distributed on [maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.luceneappengine%22)
  * Easier to configure than the [previous LAE version](https://code.google.com/p/lucene-appengine)

Coming Soon:
  * Performance improvements:
  * asynch indexing
  * buffer management

## News
  * 14-07-2024 Update LAE examples to google-cloud-firestore and a brand new demo project now powered by Micronaut and in Java 17
  * 29-06-2024 Reduced dependencies no long require Objectify
  * 02-11-2023 LAE 5 officially out (breaking changes) can be considered a new product. 
  * 30-06-2023 LAE 5 currently working fine in local environment 
  * 24-06-2023 The LAE example project will be rewritten using Micronaut framework
  * 01-04-2023 Existing examples and wiki will be completely deprecated 
  * 11-03-2023 LAE 5 will use the new google cloud infrastructure and firestore
  * 04-02-2023 LAE 5 objectify dependency is going to be removed
  * 07-01-2023 LAE 5 breaking changes confirmed
  * 16-07-2022 new LAE example will be released in the same repository 
  * 04-06-2022 LAE 5 confirmed support for Lucene 9.2
  * 24-05-2022 LAE 5 setup is going to be simplified, confirmed support for Lucene 9.1 
  * 01-05-2022 the next LAE version is going to support AppEngine Standard Environment 17
  * 13-03-2022 started development to LUCENE 9 and Java 11 and new AppEngine Standard Environment (this is not going to be ready soon)
  * 30-01-2022 tested LAE 4.5.1 with LUCENE 8.11.2
  * 21-12-2021 tested LAE 4.5.1 with LUCENE 8.11.1
  * 27-11-2021 tested LAE 4.5.1 with LUCENE 8.11.0
  * 04-10-2021 tested LAE 4.5.1 with LUCENE 8.10.0
  * 08-08-2021 released LAE 4.5.1 with LUCENE 8.9.0
  * 03-07-2021 tested LAE 4.5.0 with LUCENE 8.9.0
  * 17-04-2021 tested LAE 4.5.0 with LUCENE 8.8.2
  * 06-03-2021 tested LAE 4.5.0 with LUCENE 8.8.1 (8.8.0 test skipped)
  * 14-11-2020 tested LAE 4.5.0 with LUCENE 8.7.0
  * 17-10-2020 tested LAE 4.5.0 with LUCENE 8.6.3
  * 12-09-2020 tested LAE 4.5.0 with LUCENE 8.6.2
  * 30-08-2020 tested LAE 4.5.0 with LUCENE 8.6.1
  * 26-07-2020 tested LAE 4.5.0 with LUCENE 8.6.0
  * 05-07-2020 released LAE 4.5.0
  * 07-06-2020 tested LAE 4.4.0 with LUCENE 8.5.2
  * 18-04-2020 tested LAE 4.4.0 with LUCENE 8.5.1
  * 21-03-2020 tested LAE 4.4.0 with LUCENE 8.5.0
  * 19-01-2020 tested LAE 4.4.0 with LUCENE 8.4.1
  * 12-01-2020 tested LAE 4.4.0 with LUCENE 8.4.0
  * 08-12-2019 tested LAE 4.4.0 with LUCENE 8.3.1
  * 07-11-2019 tested LAE 4.4.0 with LUCENE 8.3.0
  * 10-08-2019 completed LAE 4.4.0 to support Lucene 8.2.x (not yet public release)
  * 07-07-2019 tested LAE 4.3.0 with LUCENE 8.1.1
  * 23-06-2019 released LAE 4.3.0 to support LUCENE 8.1.0
  * 09-06-2019 tested LAE 4.2.0 with LUCENE 7.7.2
  * 15-05-2019 tested LAE 4.2.0 with LUCENE 8.0.0
  * 02-03-2019 tested LAE 4.2.0 with LUCENE 7.7.1
  * 09-02-2019 tested LAE 4.2.0 with LUCENE 7.7.0
  * 29-12-2018 tested LAE 4.2.0 with LUCENE 7.6.0
  * 23-09-2018 tested LAE 4.2.0 with LUCENE 7.5.0
  * 14-06-2018 released LAE 4.2.0 to support LUCENE 7.4.0
  * 12-06-2018 released LAE 4.0.0 and 4.1.0 to maven central
  * 19-05-2018 tested LAE 4.1.0 with LUCENE 7.3.1
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
Check the [wiki page](../../wiki/Usage-and-Configuration). Or start from the [live demo source](https://lae-firestore.appspot.com)

## Development
If you like this project, you can [choose a kind of donation here](../../wiki/Donate) or
[![](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=LJXCLX64T7Z74&lc=GB&item_name=Lucene%20App%20Engine%20Project&item_number=LuceneAppEngine&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted) as much as you want. Thanks for your support!
