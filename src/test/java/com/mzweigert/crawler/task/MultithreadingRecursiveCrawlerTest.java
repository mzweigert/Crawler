package com.mzweigert.crawler.task;

import com.mzweigert.crawler.model.node.PageLinkType;
import com.mzweigert.crawler.model.node.PageNode;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MultithreadingRecursiveCrawlerTest {

    private static ForkJoinPool forkJoinPool = new ForkJoinPool(16);

    @Test
    public void a(){
        String url = "www.wiprodigital.com";
        //WHEN
        Collection<PageNode> result = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url));
        assertThat(result).isNotEmpty();
    }
    @Test
    public void givenURL_whenRunCrawling_thenFindPageLinkTypes() {
        //GIVEN
        String url = "https://www.wiprodigital.com";

        //WHEN
        Collection<PageNode> result = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url));

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
        Collection<PageNode> result = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url));

        //THEN
        assertThat(result).isNotEmpty();
        IntStream.range(0, 5).forEach(i -> {
            Collection<PageNode> again = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url));
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
        Collection<PageNode> invokedFromRoot = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url));
        Collection<PageNode> invokedFromSubDomain = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(sub));

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
        Collection<PageNode> first = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url_1));
        Collection<PageNode> second = forkJoinPool.invoke(new MultithreadingRecursiveCrawler(url_2));

        //THEN
        assertThat(first).isNotEmpty();
        assertThat(second).isNotEmpty();
        assertThat(first.size()).isNotEqualTo(second.size());
        assertThat(first).doesNotContainAnyElementsOf(second);
    }
}
