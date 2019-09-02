package com.mzweigert.crawler.service.crawler;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.mzweigert.crawler.FakeServerCreator;
import com.mzweigert.crawler.model.link.PageLink;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CrawlerServiceImplTest {

	private static WireMockServer server;
	private CrawlerServiceImpl service = new CrawlerServiceImpl();
	private static final String url = "http://localhost:8080";
	private static final int websiteStructureDepth = 3;

	@BeforeClass
	public static void setUpClass() {
		server = new FakeServerCreator(8080).createBinaryTreeStructureWebsite(websiteStructureDepth);
	}

	@AfterClass
	public static void clean() {
		server.stop();
	}

	@Test
	public void givenUrl_whenCrawl_thenReturnNotEmptyPageNodes() {
		//GIVEN
		CrawlerArgs args = CrawlerArgs.initBuilder()
				.withStartUrl(url)
				.build();
		//WHEN
		Collection<PageLink> result = service.crawl(args);

		//THEN
		assertThat(result).isNotEmpty();
		assertThat(result).size().isGreaterThan(1);
		assertThat(result).size().isEqualTo((int) Math.pow(2, websiteStructureDepth) - 1);
	}

	@Test
	public void givenInvalidUrl_whenCrawl_thenReturnEmptyNodes() {
		//GIVEN
		//GIVEN
		CrawlerArgs args = CrawlerArgs.initBuilder()
				.withStartUrl("invalid url")
				.build();

		//WHEN
		Collection<PageLink> result = service.crawl(args);

		//THEN
		assertThat(result).isEmpty();
	}

	@Test
	public void givenUrlWithMaxDepth_whenCrawl_thenReturnNotEmptyPageNodes() {
		//GIVEN
		int maxDepth = 2;
		CrawlerArgs args = CrawlerArgs.initBuilder()
				.withStartUrl(url)
				.withMaxDepth(maxDepth)
				.build();

		//WHEN
		Collection<PageLink> result = service.crawl(args);

		//THEN
		assertThat(result).isNotEmpty();
		assertThat(result).size().isGreaterThan(1);
		assertThat(result).size().isEqualTo((int) Math.pow(2, maxDepth) - 1);
	}

	@Test
	public void givenUrl_whenTwiceInvokeCrawlWithMaxDepthEqualToOne_thenReturnSameResults() {
		//WHEN
		CrawlerArgs args = CrawlerArgs.initBuilder()
				.withStartUrl(url)
				.withMaxDepth(websiteStructureDepth)
				.build();
		Collection<PageLink> first = service.crawl(args);
		Collection<PageLink> second = service.crawl(args);

		//THEN
		assertThat(first).isNotEmpty();
		assertThat(second).isNotEmpty();
		assertThat(first).containsAll(second);
		assertThat(second).containsAll(first);
	}

	@Test
	public void givenUrl_whenTwiceInvokeCrawlWithDifferentMaxDepth_thenReturnDifferentResults() {
		//WHEN
		CrawlerArgs args = CrawlerArgs.initBuilder()
				.withStartUrl(url)
				.withMaxDepth(websiteStructureDepth - 1)
				.build();

		Collection<PageLink> first = service.crawl(args);

		args = CrawlerArgs.initBuilder()
				.withStartUrl(url)
				.withMaxDepth(websiteStructureDepth)
				.build();
		Collection<PageLink> second = service.crawl(args);


		//THEN
		assertThat(first).isNotEmpty();
		assertThat(second).isNotEmpty();
		assertThat(first.size()).isLessThan(second.size());
	}

}