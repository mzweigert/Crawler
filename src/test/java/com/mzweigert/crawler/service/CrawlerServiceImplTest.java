package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.node.PageNode;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CrawlerServiceImplTest {

    private CrawlerServiceImpl service = new CrawlerServiceImpl();

    @Test
    public void givenUrl_whenCrawl_thenReturnNotEmptyPageNodes() {
        //GIVEN
        String url = "www.mateuszzweigert.pl";

        //WHEN
        Collection<PageNode> crawl = service.crawl(url);

        //THEN
        assertThat(crawl).isNotEmpty();
    }

    @Test
    public void givenUrl_whenCrawlWithMaxDepth_thenReturnNotEmptyPageNodes() {
        //GIVEN
        String url = "www.wiprodigital.com";

        //WHEN
        Collection<PageNode> result = service.crawl(url, 2);

        //THEN
        assertThat(result).isNotEmpty();
    }

    @Test
    public void givenUrl_whenTwiceInvokeCrawlWithMaxDepthEqualToOne_thenReturnSameResults() {
        //GIVEN
        String url = "www.wiprodigital.com";

        //WHEN
        Collection<PageNode> first = service.crawl(url, 1);
        Collection<PageNode> second = service.crawl(url, 1);


        //THEN
        assertThat(first).isNotEmpty();
        assertThat(second).isNotEmpty();
        assertThat(first).containsAll(second);
        assertThat(second).containsAll(first);
    }

    @Test
    public void givenUrl_whenTwiceInvokeCrawlWithDifferentMaxDepth_thenReturnDifferentResults() {
        //GIVEN
        String url = "www.wiprodigital.com";

        //WHEN
        Collection<PageNode> first = service.crawl(url, 2);
        Collection<PageNode> second = service.crawl(url, 3);


        //THEN
        assertThat(first).isNotEmpty();
        assertThat(second).isNotEmpty();
        assertThat(first.size()).isLessThan(second.size());
    }
}