Lucene App Engine project (aka LAE)
===============

This project provides a directory useful to build Lucene and Google App Engine powered applications.

![https://lucene.apache.org/images/lucene_logo_green_300.png](https://lucene.apache.org/images/lucene_logo_green_300.png) ![https://www.google.com/accounts/ah/appengine.jpg](https://www.google.com/accounts/ah/appengine.jpg)

Main Features:
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
  * Yes! It works, see it in action ([live demo site](https://bigtable-lucene.appspot.com))
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
  * 05-07-2015: Released New LAE-3.0.0 compatible with Lucene 5.0.0 (this is a major release with breaking changes)
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
Check the [wiki page](../../wiki/Usage-and-Configuration)

## Development
If you like this project, you can [choose a kind of donation here](Donate.md) or give a free donation
[![](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=LJXCLX64T7Z74&lc=GB&item_name=Lucene%20App%20Engine%20Project&item_number=LuceneAppEngine&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted). Thanks for your support!
