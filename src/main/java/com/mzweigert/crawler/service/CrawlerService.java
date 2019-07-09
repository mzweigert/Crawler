package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.node.PageNode;

import java.util.Collection;

public interface CrawlerService {

    int DEFAULT_MAX_DEPTH = 100;

    /**
     * Method start crawling from given as param url to {@value #DEFAULT_MAX_DEPTH} of node url
     * @param startUrl start url
     * @return collections of discovered page nodes with information about founded url and resource type
     */
    Collection<PageNode> crawl(String startUrl);

    /**
     * Method crawl given as param url to n-child of node url given as max depth
     * @param url start url
     * @param maxDepth maxDepth of n-child node
     * @return collections of discovered page nodes with information about founded url and resource type
     */
    Collection<PageNode> crawl(String url, int maxDepth);
}
