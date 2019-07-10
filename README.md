# Crawler
Simple crawler based on [ForkJoinPool](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html) tasks.

## Software needed to run:

* Java: `1.8` [How to install](https://java.com/en/download/help/download_options.xml)
* Maven: `3.3.x` [How to install](https://maven.apache.org/install.html)

## Build project
Make sure, that you have installed maven and java, then in project root  folder type:
`mvn clean install`

## How to run

To run crawler run crawler.bat (Windows) or crawler.sh (Unix/Linux) with arguments given bellow:
<pre>
usage: Windows: crawler -u [-d] | Linux/Unix: ./crawler.sh -u [-d]
 -u,--url       Initial url from which crawler start. 
                Url should has "http://" or "https://" prefix.
 -d,--depth     Depth level of the crawler search. 
                Default value is 100 [optional]
 -g,--grouped   Grouping found links by <a href="https://github.com/mzweigert/Crawler/blob/master/src/main/java/com/mzweigert/crawler/model/node/PageLinkType.java">PageLinkType</a>
                and save them to separate files.
</pre>

## Result files
When crawler finishes work, discovered links are saved as xml files to %root_project_folder%/output/%given_url_as_param%/

