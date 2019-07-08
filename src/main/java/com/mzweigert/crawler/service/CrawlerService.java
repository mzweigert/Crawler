package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.PageNode;

import java.io.IOException;

public interface CrawlerService {

    int MAX_DEPTH = 100;

    PageNode crawl(String url) throws IOException;
}
