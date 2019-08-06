package com.mzweigert.crawler.service.crawler;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.mzweigert.crawler.FakeServerCreator;
import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.model.link.PageLinkType;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CrawlerTaskTest {

    private static ForkJoinPool forkJoinPool = new ForkJoinPool(16);
    private static int maxDepth = CrawlerService.DEFAULT_MAX_DEPTH;
    private static int documentsPerWorker = CrawlerService.DOCUMENTS_PER_WORKER;
	private WireMockServer server;
    private static final String url = "http://localhost:8080";

    @After
	public void clean() {
    	if(server != null && server.isRunning()) {
    		server.stop();
		}
	}

    @Test
    public void givenURL_whenRunCrawling_thenFindPageLinkTypes() {
		server = new FakeServerCreator(8080).createFlatStructureWebsiteWithAllResources();

		//WHEN
        Collection<PageLink> result = forkJoinPool.invoke(new CrawlerTask(url, maxDepth, documentsPerWorker));

        assertThat(result).isNotEmpty();
        Map<PageLinkType, List<PageLink>> groupedByType = result.stream()
                .collect(Collectors.groupingBy(PageLink::getType));

        //THEN
		Arrays.stream(PageLinkType.values()).forEach(type -> {
            List<PageLink> linksByType = groupedByType.get(type);
            assertThat(linksByType).isNotEmpty();
        });

    }

    @Test
    public void givenRootUrl_whenRunCrawlingSixTimes_thenAlwaysReturnSameResult() {
		server = new FakeServerCreator(8080).create(3, 5);

        //WHEN
        Collection<PageLink> result = forkJoinPool.invoke(new CrawlerTask(url, maxDepth, documentsPerWorker));

        //THEN
		assertThat(result).isNotEmpty();
        IntStream.range(0, 5).forEach(i -> {
            Collection<PageLink> again = forkJoinPool.invoke(new CrawlerTask(url, maxDepth, documentsPerWorker));
            assertThat(again).isNotEmpty();
            assertThat(result.size()).isEqualTo(again.size());
            assertThat(result).containsAll(again);
        });
	}

    @Test
    public void givenDifferentUrls_whenRunCrawlingTwice_thenFindDifferentLinks() {
		server = new FakeServerCreator(8080).create(3, 5);
		WireMockServer wireMockServer = new FakeServerCreator(8090).create(5, 3);

		//WHEN
        Collection<PageLink> first = forkJoinPool.invoke(new CrawlerTask("localhost:8080/", maxDepth, documentsPerWorker));
        Collection<PageLink> second = forkJoinPool.invoke(new CrawlerTask("localhost:8090/", maxDepth, documentsPerWorker));

        //THEN
		wireMockServer.stop();
        assertThat(first).isNotEmpty();
        assertThat(second).isNotEmpty();
        assertThat(first.size()).isNotEqualTo(second.size());
        assertThat(first).doesNotContainAnyElementsOf(second);
    }
}
