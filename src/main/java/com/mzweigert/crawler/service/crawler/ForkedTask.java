/*
 * Copyright (c) 2019. BEST S.A. and/or its affiliates. All rights reserved.
 */
package com.mzweigert.crawler.service.crawler;


import com.mzweigert.crawler.model.VisitedLinks;
import com.mzweigert.crawler.model.link.PageLink;

import java.util.List;

class ForkedTask {
	private CrawlerTask left;
	private CrawlerTask right;

	ForkedTask(CrawlerArgs args, List<PageLink> toVisitFiltered, VisitedLinks visitedLinks) {
		this.fork(args, toVisitFiltered, visitedLinks);
	}

	CrawlerTask getLeft() {
		return left;
	}

	CrawlerTask getRight() {
		return right;
	}

	private ForkedTask fork(CrawlerArgs args, List<PageLink> toVisitFiltered, VisitedLinks visitedLinks) {
		List<PageLink> firstPart = toVisitFiltered.subList(0, toVisitFiltered.size() / 2);
		List<PageLink> secondPart = toVisitFiltered.subList(toVisitFiltered.size() / 2, toVisitFiltered.size());

		left = new CrawlerTask(firstPart, visitedLinks, args);
		right = new CrawlerTask(secondPart, visitedLinks, args);
		return this;
	}
}