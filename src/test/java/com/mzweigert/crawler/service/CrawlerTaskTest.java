package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.node.PageLinkType;
import com.mzweigert.crawler.model.node.PageNode;
import org.junit.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CrawlerTaskTest {

    private static ForkJoinPool forkJoinPool = new ForkJoinPool(16);
    private static int maxDepth = CrawlerService.DEFAULT_MAX_DEPTH;

    @Test
    public void givenURL_whenRunCrawling_thenFindPageLinkTypes() {
        //GIVEN
        String url = "https://www.wiprodigital.com";

        //WHEN
        Collection<PageNode> result = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));

        assertThat(result).isNotEmpty();
        Map<PageLinkType, List<PageNode>> groupedByType = result.stream()
                .collect(Collectors.groupingBy(PageNode::getType));

        EnumSet.of(PageLinkType.INTERNAL_ROOT_DOMAIN, PageLinkType.INTERNAL_SUB_DOMAIN,
                PageLinkType.EXTERNAL_RESOURCES, PageLinkType.EXTERNAL_DOMAIN,
                PageLinkType.INVALID_LINK)
                .forEach(type -> {
                    List<PageNode> linksByType = groupedByType.get(type);
                    assertThat(linksByType).isNotEmpty();
                });
    }

    @Test
    public void givenRootUrl_whenRunCrawlingSixTimes_thenAlwaysReturnSameResult() {
        //GIVEN
        String url = "https://www.wiprodigital.com";

        //WHEN
        Collection<PageNode> result = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));

        //THEN
        assertThat(result).isNotEmpty();
        IntStream.range(0, 5).forEach(i -> {
            Collection<PageNode> again = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));
            assertThat(again).isNotEmpty();
            assertThat(result.size()).isEqualTo(again.size());
            assertThat(result).containsAll(again);
        });
    }

    @Test
    public void givenRootUrlAndSubDomainUrl_whenRunCrawlingTwice_thenFindSameLinks() {
        //GIVEN
        String url = "https://www.wiprodigital.com";
        String sub = "https://www.wiprodigital.com/what-we-do/";

        //WHEN
        Collection<PageNode> invokedFromRoot = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));
        Collection<PageNode> invokedFromSubDomain = forkJoinPool.invoke(new CrawlerTask(sub, maxDepth));

        //THEN
        assertThat(invokedFromRoot).isNotEmpty();
        assertThat(invokedFromSubDomain).isNotEmpty();
        assertThat(invokedFromRoot.size()).isEqualTo(invokedFromSubDomain.size());
        assertThat(invokedFromRoot).containsAll(invokedFromSubDomain);
    }

    @Test
    public void givenDifferentUrls_whenRunCrawlingTwice_thenFindDifferentLinks() {
        //GIVEN
        String url_1 = "https://www.wiprodigital.com";
        String url_2 = "https://www.best.com.pl";

        //WHEN
        Collection<PageNode> first = forkJoinPool.invoke(new CrawlerTask(url_1, maxDepth));
        Collection<PageNode> second = forkJoinPool.invoke(new CrawlerTask(url_2, maxDepth));

        //THEN
        assertThat(first).isNotEmpty();
        assertThat(second).isNotEmpty();
        assertThat(first.size()).isNotEqualTo(second.size());
        assertThat(first).doesNotContainAnyElementsOf(second);
    }
}
