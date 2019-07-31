package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.model.link.PageLink;

import java.util.Collection;

public interface CrawlerService {

    int DEFAULT_MAX_DEPTH = 100;
    int DOCUMENTS_PER_WORKER = 5;

    /**
     * Method start crawling from given as param url to {@value #DEFAULT_MAX_DEPTH} of link url
     *
     * @param startUrl start url
     * @return collections of discovered page nodes with information about founded url and resource type
     */
    Collection<PageLink> crawl(String startUrl);

    /**
     * Method crawl given as param url to n-child of link url given as max depth
     *
     * @param url      start url
     * @param maxDepth maxDepth of n-child link
     * @return collections of discovered page nodes with information about founded url and resource type
     */
    Collection<PageLink> crawl(String url, int maxDepth);
}
