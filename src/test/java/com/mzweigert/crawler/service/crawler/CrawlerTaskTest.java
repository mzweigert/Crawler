package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.model.node.PageLink;
import com.mzweigert.crawler.model.node.PageLinkType;
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
        Collection<PageLink> result = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));

        assertThat(result).isNotEmpty();
        Map<PageLinkType, List<PageLink>> groupedByType = result.stream()
                .collect(Collectors.groupingBy(PageLink::getType));

        EnumSet.of(PageLinkType.INTERNAL_ROOT_DOMAIN, PageLinkType.INTERNAL_SUB_DOMAIN,
                PageLinkType.EXTERNAL_RESOURCES, PageLinkType.EXTERNAL_DOMAIN,
                PageLinkType.INVALID_LINK)
                .forEach(type -> {
                    List<PageLink> linksByType = groupedByType.get(type);
                    assertThat(linksByType).isNotEmpty();
                });
    }

    @Test
    public void givenRootUrl_whenRunCrawlingSixTimes_thenAlwaysReturnSameResult() {
        //GIVEN
        String url = "https://www.wiprodigital.com";

        //WHEN
        Collection<PageLink> result = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));

        //THEN
        assertThat(result).isNotEmpty();
        IntStream.range(0, 5).forEach(i -> {
            Collection<PageLink> again = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));
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
        Collection<PageLink> invokedFromRoot = forkJoinPool.invoke(new CrawlerTask(url, maxDepth));
        Collection<PageLink> invokedFromSubDomain = forkJoinPool.invoke(new CrawlerTask(sub, maxDepth));

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
        Collection<PageLink> first = forkJoinPool.invoke(new CrawlerTask(url_1, maxDepth));
        Collection<PageLink> second = forkJoinPool.invoke(new CrawlerTask(url_2, maxDepth));

        //THEN
        assertThat(first).isNotEmpty();
        assertThat(second).isNotEmpty();
        assertThat(first.size()).isNotEqualTo(second.size());
        assertThat(first).doesNotContainAnyElementsOf(second);
    }
}
