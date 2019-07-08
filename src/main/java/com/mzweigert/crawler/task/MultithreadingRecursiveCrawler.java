package com.mzweigert.crawler.task;

import com.mzweigert.crawler.model.PageLinkType;
import com.mzweigert.crawler.model.PageNode;
import com.mzweigert.crawler.model.PageNodeMapper;
import com.mzweigert.crawler.util.UrlUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

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

		Optional<PageNode> possibleFileOrError = UrlUtil.findDownloadOrErrorLink(url, connect);
		if (possibleFileOrError.isPresent()) {
			allVisited.add(possibleFileOrError.get());
		} else {
			URL asUrl = connect.response().url();
			this.rootUrl = UrlUtil.extractRootUrl(asUrl);
			PageNode root = new PageNode(asUrl.toString(), PageLinkType.INTERNAL_ROOT_DOMAIN);
			ArrayList<PageNode> toVisit = new ArrayList<>();
			toVisit.add(root);
			this.toVisit = extractLinks(toVisit, rootUrl);
		}
	}


	private MultithreadingRecursiveCrawler(String rootUrl, Collection<PageNode> toVisit, Set<PageNode> allVisited) {
		this.rootUrl = rootUrl;
		this.toVisit = toVisit;
		this.allVisited = allVisited;
	}

	private Set<PageNode> extractLinks(Collection<PageNode> toVisit, String rootUrl) {
		Set<String> notVisited = new HashSet<>();

		for (PageNode node : toVisit) {
			try {
				if(allVisited.contains(node)){
					continue;
				}
				Connection connect = Jsoup.connect(node.getDomainUrl()).ignoreContentType(true);
				allVisited.add(node);
				if(node.isInternalDomain()){
					Set<String> nodes = LinksFinder.getInstance(connect.get()).find(notVisited);
					notVisited.addAll(nodes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return PageNodeMapper.map(rootUrl, allVisited, notVisited);
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
