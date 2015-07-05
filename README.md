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
  * 05-07-2015: now build with the new released LAE 3.0.0 and LUCENE 5.0.0, this is a major release with breaking changes check below the details
  * 17-09-2014 Released New LAE-2.2.0 compatible with Lucene 4.10.x and Lucene 4.9.x (needs Java 7)
  * 10-08-2014 Released New LAE-2.1.0 compatible with Lucene 4.9.x  (needs Java 7)
  * 23-07-2014 Released New LAE-2.0.1 **bugfix** and **performance improvements** release, **all other versions** are now **deprecated**
  * 20-05-2014 Released New LAE-2.0.0 compatible with Lucene 4.8.x  (needs Java 7)
  * 05-04-2014 Released New LAE-1.2.0 compatible with Lucene 4.7.x
  * 05-04-2014 Released New LAE-1.1.0 compatible only with Lucene 4.6.x
  * 03-04-2014 Migration of source code completed on [GitHub](https://github.com/UltimaPhoenix/luceneappengine)
  * 03-04-2014 Released New LAE-1.0.3 on [Maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.luceneappengine%22)
  * 03-04-2014 Discontinued Objecfity 3 support, if you need it refer to the [old site](https://code.google.com/p/lucene-appengine)
  * 03-04-2014 Discontinued Lucene 3.6.x support, if you need it refer to the [old site](https://code.google.com/p/lucene-appengine)


## Usage
```
GaeDirectory directory = new GaeDirectory();//create a default index
IndexWriterConfig config = GaeLuceneUtil.getIndexWriterConfig(analyzer);//get configuration if you are using a LAE version less than 3.x.x add LUCENE_VERSION as parameter
IndexWriter w = new IndexWriter(directory, config);//get the writer
/* now use Apache Lucene like you're used to */
```

In order to manage multiple index instances simply create multiple [GaeDirectory](http://maven-site.lucene-appengine.googlecode.com/hg/apidocs/com/googlecode/lucene/appengine/GaeDirectory.html) instances with different names:
```
GaeDirectory directory = new GaeDirectory("anotherIndexName");
```

A complete example with source code [lucene-appengine-examples](http://code.google.com/p/lucene-appengine-examples)

## Configuration
Add into your _WEB-INF/web.xml_ the objectify servlet (see [objectify documenation for details and alternatives](https://code.google.com/p/objectify-appengine/wiki/Setup#Enable_ObjectifyFilter_for_your_requests)):
```
<web-app …>
    …
    <filter>
        <filter-name>ObjectifyFilter</filter-name>
        <filter-class>com.googlecode.objectify.ObjectifyFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>ObjectifyFilter</filter-name>
        <url-pattern>*</url-pattern>
    </filter-mapping>
    …
</web-app>
```

Add into your _WEB-INF/appengine-web.xml_:
```
<appengine-web-app>
....
    <class-loader-config>
        <priority-specifier filename="luceneappengine-[LAE Version].jar"/>
    </class-loader-config>
.....
    <system-properties>
        ...
        <property name="os.version" value="1.0.GAE whatever" />
        <property name="os.arch" value="GAE whatever" />
    </system-properties>
</appengine-web-app>
```
**WARNING**: If you are using a SNAPSHOT version of LAE using Maven, you must add the specific jar name of the snapshot, you can see the specific jar name typing on command line _'mvn clean package && ls -lR target/ | grep lucene-appengine'_

## Maven
If you are using maven to build/deploy google app engine war add this snippet to import the lucene directory
```
<dependencies>
	<!-- Declare dependencies in this order -->
	<dependency>
		<groupId>com.googlecode.luceneappengine</groupId>
		<artifactId>luceneappengine</artifactId>
		<version>3.0.0</version>
	</dependency>
	<dependency>
		<groupId>org.apache.lucene</groupId>
		<artifactId>lucene-core</artifactId>
		<version>5.0.0</version>
	</dependency>
	<dependency>
		<groupId>org.apache.lucene</groupId>
		<artifactId>lucene-analyzers-common</artifactId>
		<version>5.0.0</version>
	</dependency>
....
</dependencies>
```

For a complete example and source-code see [LAE Examples project](http://code.google.com/p/lucene-appengine-examples/)

## Development
If you like this project, you can [choose a kind of donation here](Donate.md) or give a free donation
[![](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=LJXCLX64T7Z74&lc=GB&item_name=Lucene%20App%20Engine%20Project&item_number=LuceneAppEngine&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted). Thanks for your support!
