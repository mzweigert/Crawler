package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.model.VisitedLinks;
import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.model.link.PageLinkMapper;
import com.mzweigert.crawler.model.link.PageLinkType;
import com.mzweigert.crawler.util.AttributeFinder;
import com.mzweigert.crawler.util.UrlUtil;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrawlerTask extends RecursiveTask<Collection<PageLink>> {

	private Collection<PageLink> toVisit;
	private VisitedLinks visitedLinks;
	private CrawlerArgs args;

	CrawlerTask(CrawlerArgs args) {
		URL url = UrlUtil.asURL(args.getStartUrl());
		if (url == null) {
			this.visitedLinks = new VisitedLinks(args.getStartUrl());
			return;
		}

		String rootUrl = UrlUtil.extractRootUrl(url);
		this.visitedLinks = new VisitedLinks(rootUrl);
		String link = UrlUtil.normalizeLink(rootUrl, url.toString());
		PageLink root = PageLinkMapper.map(rootUrl, link);
		this.toVisit = new ArrayList<PageLink>(1) {{
			add(root);
		}};
		this.args = args;
	}

	CrawlerTask(Collection<PageLink> toVisit, VisitedLinks visitedLinks, CrawlerArgs args) {
		this.toVisit = toVisit;
		this.visitedLinks = visitedLinks;
		this.args = args;
	}

	private void onException(String url, Exception e) {
		if (cannotOpenPage(e)) {
			PageLink node = new PageLink(url, PageLinkType.INVALID_LINK);
			if (visitedLinks.notContains(url)) {
				visitedLinks.add(node);
				System.out.println("Cannot fetch url: " + node.getUrl());
			}

		} else {
			System.out.println("Exception for url " + url + ": " + e.getClass() + " " + e.getMessage());
		}
	}

	private boolean cannotOpenPage(Exception e) {
		if (!(e instanceof HttpStatusException)) return false;
		int statusCode = ((HttpStatusException) e).getStatusCode();
		return IntStream.of(404, 403).anyMatch(code -> statusCode == code);
	}

	@Override
	protected Collection<PageLink> compute() {
		if (toVisit == null || toVisit.isEmpty() || args.getMaxDepth() <= 0) {
			return visitedLinks.nodes();
		}

		List<PageLink> toVisitFiltered = toVisit.stream()
				.filter(link -> !visitedLinks.contains(link.getUrl()))
				.collect(Collectors.toList());

		if (toVisitFiltered.size() > args.getDocumentsPerThread()) {
			ForkedTask forkedTask = new ForkedTask(args, toVisitFiltered, visitedLinks);
			CrawlerTask left = forkedTask.getLeft();
			CrawlerTask right = forkedTask.getRight();

			invokeAll(left, right);

		} else if (!toVisitFiltered.isEmpty()) {
			createTaskFromExtractedLinks(toVisitFiltered)
					.ifPresent(CrawlerTask::compute);
		}

		return visitedLinks.nodes();
	}

	private Optional<CrawlerTask> createTaskFromExtractedLinks(List<PageLink> toVisitFiltered) {
		Collection<PageLink> notVisited = extractLinks(toVisitFiltered);

		if (notVisited.isEmpty()) {
			return Optional.empty();
		}

		CrawlerArgs newArgs = CrawlerArgs.initBuilder()
				.withStartUrl(args.getStartUrl())
				.withMaxDepth(args.getMaxDepth() - 1)
				.withDocumentsPerThread(args.getDocumentsPerThread())
				.withSelectors(args.getAdditionSelectors())
				.build();

		return Optional.of(new CrawlerTask(notVisited, visitedLinks, newArgs));
	}

	private Set<PageLink> extractLinks(Collection<PageLink> toVisit) {
		Set<String> notVisited = new HashSet<>();

		for (PageLink node : toVisit) {
			try {
				if (visitedLinks.contains(node.getUrl())) {
					continue;
				}
				if (node.isInternalDomain()) {
					Connection connect = Jsoup.connect(node.getUrl()).ignoreContentType(true);
					Set<String> nodes = AttributeFinder.getInstance(connect.get())
							.find(notVisited, args.getAdditionSelectorsAsArray());
					notVisited.addAll(nodes);
				}
				visitedLinks.add(node);

			} catch (Exception e) {
				onException(node.getUrl(), e);
			}
		}
		return PageLinkMapper.mapMany(visitedLinks, notVisited);
	}

}
