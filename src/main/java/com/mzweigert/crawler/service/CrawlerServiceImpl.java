package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.node.PageNode;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

public class CrawlerServiceImpl implements CrawlerService {

    private Properties properties = new Properties();
    private ForkJoinPool forkJoinPool;
    private int maxDepth;

    public CrawlerServiceImpl() {
        try {
            properties.load(getClass()
                    .getClassLoader()
                    .getResourceAsStream("config.properties")
            );
            forkJoinPool = initForkJoinPool();
            maxDepth = initMaxDepth();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int initMaxDepth() {
        Object maxDepth = properties.get("max_depth");
        if (maxDepth != null) {
            return Integer.valueOf(maxDepth.toString());
        } else {
            return DEFAULT_MAX_DEPTH;
        }
    }

    private ForkJoinPool initForkJoinPool() {
        Object workers = properties.get("workers");
        if (workers != null) {
            return new ForkJoinPool(Integer.valueOf(workers.toString()));
        } else {
            return new ForkJoinPool();
        }
    }

    public Collection<PageNode> crawl(String startUrl) {
        return forkJoinPool.invoke(new CrawlerTask(startUrl, maxDepth));
    }

    public Collection<PageNode> crawl(String startUrl, int maxDepth) {
        return forkJoinPool.invoke(new CrawlerTask(startUrl, maxDepth));
    }

}
