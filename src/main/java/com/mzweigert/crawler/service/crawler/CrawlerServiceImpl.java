package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.configuration.Configuration;
import com.mzweigert.crawler.model.link.PageLink;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

public class CrawlerServiceImpl implements CrawlerService {

    private ForkJoinPool forkJoinPool;

    public CrawlerServiceImpl() {
        Integer workers = Integer.valueOf(Configuration.getProperty("workers"));
        forkJoinPool = new ForkJoinPool(workers);
    }

    @Override
    public Collection<PageLink> crawl(CrawlerArgs args) {
        return forkJoinPool.invoke(new CrawlerTask(args));
    }

}
