package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.model.link.PageLink;

import java.util.Collection;

public interface CrawlerService {

    /**
     * Method start crawling from given as param url
     *
     * @param args with configurations properties
     * @return collections of discovered page nodes with information about founded url and resource type
     */
    Collection<PageLink> crawl(CrawlerArgs args);

}
