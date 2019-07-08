package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.PageNode;
import org.junit.Test;

import java.io.IOException;

public class RecursiveCrawlerServiceImplTest {

    private CrawlerService service = new RecursiveCrawlerServiceImpl();

    @Test
    public void crawl() throws IOException {

        PageNode node = service.crawl("http://mateuszzweigert.pl");
        System.out.println(node);
    }
}