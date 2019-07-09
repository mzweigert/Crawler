package com.mzweigert.crawler.task;

import com.mzweigert.crawler.model.node.PageLinkType;
import com.mzweigert.crawler.model.node.PageNode;
import com.mzweigert.crawler.model.node.PageNodeMapper;
import com.mzweigert.crawler.model.VisitedLinks;
import com.mzweigert.crawler.util.UrlUtil;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class MultithreadingRecursiveCrawler extends RecursiveTask<Collection<PageNode>> {

	private static final int THRESHOLD = 5;

	private Collection<PageNode> toVisit;
	private VisitedLinks visitedLinks;

	MultithreadingRecursiveCrawler(String url) {
		try {
			if(!url.startsWith("http")){
				url = "http://" + url;
			}
			URL asUrl = Jsoup.connect(url).ignoreContentType(true).execute().url();
			String rootUrl = UrlUtil.extractRootUrl(asUrl);
			this.visitedLinks = new VisitedLinks(rootUrl);
			url = UrlUtil.normalizeLink(rootUrl, asUrl.toString());
			PageNode root = PageNodeMapper.map(rootUrl, url);
			ArrayList<PageNode> toVisit = new ArrayList<PageNode>() {{ add(root); }};
			this.toVisit = extractLinks(toVisit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MultithreadingRecursiveCrawler(Collection<PageNode> toVisit, VisitedLinks visitedLinks) {
		this.toVisit = toVisit;
		this.visitedLinks = visitedLinks;
	}

	private Set<PageNode> extractLinks(Collection<PageNode> toVisit) {
		Set<String> notVisited = new HashSet<>();

		for (PageNode node : toVisit) {
			try {
				if (visitedLinks.contains(node.getDomainUrl())) {
					continue;
				}
				if (node.isInternalDomain()) {
					Connection connect = Jsoup.connect(node.getDomainUrl()).ignoreContentType(true);
					Set<String> nodes = AttributeFinder.getInstance(connect.get()).find(notVisited);
					notVisited.addAll(nodes);
				}
				visitedLinks.add(node);

			} catch (Exception e) {
				onException(node.getDomainUrl(), e);
			}
		}
		return PageNodeMapper.mapMany(visitedLinks, notVisited);
	}

	private void onException(String url, Exception e) {
		if(cannotFoundPageException(e)){
			PageNode node = new PageNode(url, PageLinkType.INVALID_LINK);
			if(visitedLinks.notContains(url)){
				visitedLinks.add(node);
				System.out.println("Page not found: " + node.getDomainUrl());
			}

		} else {
			e.printStackTrace();
		}
	}

	private boolean cannotFoundPageException(Exception e) {
		return e instanceof HttpStatusException && ((HttpStatusException) e).getStatusCode() == 404;
	}

	@Override
	protected Collection<PageNode> compute() {
		if (toVisit == null || toVisit.isEmpty()) {
			return visitedLinks.nodes();
		}

		if (toVisit.size() > THRESHOLD) {
			List<PageNode> toVisitAsList = new ArrayList<>(toVisit);
			List<PageNode> firstPart = toVisitAsList.subList(0, toVisitAsList.size() / 2);
			List<PageNode> secondPart = toVisitAsList.subList(toVisitAsList.size() / 2, toVisitAsList.size());

			MultithreadingRecursiveCrawler left = new MultithreadingRecursiveCrawler(firstPart, visitedLinks);
			MultithreadingRecursiveCrawler right = new MultithreadingRecursiveCrawler(secondPart, visitedLinks);

			right.fork();
			left.compute();
			right.join();

		} else {
			Collection<PageNode> notVisited = extractLinks(toVisit);
			if (!notVisited.isEmpty()) {
				MultithreadingRecursiveCrawler cr = new MultithreadingRecursiveCrawler(notVisited, visitedLinks);
				cr.compute();
			}
		}

		return visitedLinks.nodes();
	}

}
