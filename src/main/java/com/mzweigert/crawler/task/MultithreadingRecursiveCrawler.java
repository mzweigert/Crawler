package com.mzweigert.crawler.task;

import com.mzweigert.crawler.model.PageLinkType;
import com.mzweigert.crawler.model.PageNode;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class MultithreadingRecursiveCrawler extends RecursiveTask<Set<PageNode>> {

	private static final int THRESHOLD = 3;

	private String rootUrl;
	private Collection<PageNode> toVisit;
	private Set<PageNode> allVisited;

	MultithreadingRecursiveCrawler(String url) throws IOException {
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		Connection connect = Jsoup.connect(url).ignoreContentType(true);
		this.allVisited = Collections.synchronizedSet(new HashSet<>());

		Optional<PageNode> possibleFileOrError = findDownloadOrErrorLink(url, connect);
		if (possibleFileOrError.isPresent()) {
			allVisited.add(possibleFileOrError.get());
		} else {
			URL asUrl = connect.response().url();
			this.rootUrl = extractRootUrl(asUrl);
			PageNode root = new PageNode(asUrl.toString(), PageLinkType.INTERNAL_MAIN_DOMAIN);
			ArrayList<PageNode> toVisit = new ArrayList<>();
			toVisit.add(root);
			this.toVisit = extractLinks(toVisit, rootUrl);
		}
	}

	private String extractRootUrl(URL url) {
		return url.getProtocol() +
				(url.getPort() > 0 ? ":" + url.getPort() : "") +
				"://" +
				url.getHost();
	}

	private MultithreadingRecursiveCrawler(String rootUrl, Collection<PageNode> toVisit, Set<PageNode> allVisited) {
		this.rootUrl = rootUrl;
		this.toVisit = toVisit;
		this.allVisited = allVisited;
	}

	private Set<PageNode> extractLinks(Collection<PageNode> toVisit, String rootUrl) {
		Set<PageNode> notVisited = new HashSet<>();

		for (PageNode node : toVisit) {
			try {
				if(allVisited.contains(node)){
					continue;
				}
				Connection connect = Jsoup.connect(node.getDomainUrl()).ignoreContentType(true);
				Optional<PageNode> possibleFileOrError = findDownloadOrErrorLink(node.getDomainUrl(), connect);
				if (possibleFileOrError.isPresent()) {
					continue;
				}
				allVisited.add(node);
				Set<PageNode> nodes = extractFromDocument(rootUrl, connect.get());
				notVisited.addAll(nodes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return notVisited.stream()
				.filter(node -> !allVisited.contains(node))
				.collect(Collectors.toSet());
	}

	private Set<PageNode> extractFromDocument(String mainDomainUrl, Document document) {
		return document.select("a[href]")
				.stream()
				.map(a -> a.attr("href"))
				.filter(link -> link.lastIndexOf("#") < 0)
				.filter(link -> link.lastIndexOf("?") < 0)
				.filter(link -> link.length() > 1)
				.map(link -> {
					if (link.startsWith("/")) {
						link = mainDomainUrl + link;
					} else if (link.charAt(link.length() - 1) == '/') {
						link = link.substring(0, link.length() - 1);
					}
					return new PageNode(link, PageLinkType.INTERNAL_SUB_DOMAIN);
				})
				.filter(node -> !allVisited.contains(node))
				.filter(pageNode -> pageNode.getDomainUrl().startsWith(mainDomainUrl))
				.collect(Collectors.toSet());
	}

	private Optional<PageNode> findDownloadOrErrorLink(String url, Connection connect) throws IOException {
		Connection.Response response;
		try {
			response = connect.execute();
		} catch (HttpStatusException e) {
			return Optional.of(new PageNode(url, PageLinkType.INTERNAL_ERROR_PAGE));
		}

		if (!response.contentType().startsWith("text")) {
			return Optional.of(new PageNode(url, PageLinkType.INTERNAL_RESOURCES));
		}
		return Optional.empty();
	}

	@Override
	protected Set<PageNode> compute() {
		if(toVisit == null || toVisit.isEmpty()){
			return allVisited;
		}

		if (toVisit.size() > THRESHOLD) {
			List<PageNode> toVisitAsList = new ArrayList<>(toVisit);
			List<PageNode> firstPart = toVisitAsList.subList(0, toVisitAsList.size() / 2);
			List<PageNode> secondPart = toVisitAsList.subList(toVisitAsList.size() / 2, toVisitAsList.size());

			MultithreadingRecursiveCrawler left = new MultithreadingRecursiveCrawler(rootUrl, firstPart, allVisited);
			MultithreadingRecursiveCrawler right = new MultithreadingRecursiveCrawler(rootUrl, secondPart, allVisited);

			right.fork();
			left.compute();
			right.join();

		} else {
			Collection<PageNode> notVisited = extractLinks(toVisit, rootUrl);
			if(!notVisited.isEmpty()) {
				MultithreadingRecursiveCrawler cr = new MultithreadingRecursiveCrawler(rootUrl, notVisited, allVisited);
				cr.compute();
			}
		}

		return allVisited;
	}

}
