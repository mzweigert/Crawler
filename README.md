# Crawler
Simple crawler

## Software usage:

* Java: `1.8`
* Maven: `3.3.x`

## Build project
Make sure, that you have installed maven and java, then in project root  folder type:
`mvn clean install`

## How to run

To run crawler run crawler.bat (Windows) or crawler.sh (Unix/Linux) with arguments given bellow:
<pre>
usage: Windows: crawler -u [-d] | Linux/Unix: ./crawler.sh -u [-d]
 -u,--url    Initial url from which crawler start. 
             Url should has "http://" or "https://" prefix.
 -d,--depth  Depth level of the crawler search. 
             Default value is 100 [optional]
</pre>