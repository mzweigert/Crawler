package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.model.link.PageLink;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

public class CrawlerServiceImpl implements CrawlerService {

    private Properties properties = new Properties();
    private ForkJoinPool forkJoinPool;
    private int maxDepth, documentsPerWorker;

    public CrawlerServiceImpl() {
        try {
            properties.load(getClass()
                    .getClassLoader()
                    .getResourceAsStream("config.properties")
            );
            forkJoinPool = initForkJoinPool();
            maxDepth = initMaxDepth();
            documentsPerWorker = initDocumentsPerWorkers();
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

    private int initDocumentsPerWorkers() {
        Object documentsPerWorker = properties.get("documents_per_worker");
        if (documentsPerWorker != null) {
            return Integer.valueOf(documentsPerWorker.toString());
        } else {
            return DOCUMENTS_PER_WORKER;
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

    public Collection<PageLink> crawl(String startUrl) {
        return forkJoinPool.invoke(new CrawlerTask(startUrl, maxDepth, documentsPerWorker));
    }

    public Collection<PageLink> crawl(String startUrl, int maxDepth) {
        return forkJoinPool.invoke(new CrawlerTask(startUrl, maxDepth, documentsPerWorker));
    }

    public Collection<PageLink> crawl(String startUrl, int maxDepth, int documentsPerWorker) {
        return forkJoinPool.invoke(new CrawlerTask(startUrl, maxDepth, documentsPerWorker));
    }

}
