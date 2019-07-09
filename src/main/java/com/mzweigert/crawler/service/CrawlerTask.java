package com.mzweigert.crawler.service;

import com.mzweigert.crawler.model.VisitedLinks;
import com.mzweigert.crawler.model.node.PageLinkType;
import com.mzweigert.crawler.model.node.PageNode;
import com.mzweigert.crawler.model.node.PageNodeMapper;
import com.mzweigert.crawler.util.AttributeFinder;
import com.mzweigert.crawler.util.UrlUtil;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class CrawlerTask extends RecursiveTask<Collection<PageNode>> {

    private static final int THRESHOLD = 5;

    private Collection<PageNode> toVisit;
    private VisitedLinks visitedLinks;
    private final int depth;

    CrawlerTask(String url, int maxDepth) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        this.depth = maxDepth;
        try {
            URL asUrl = Jsoup.connect(url).ignoreContentType(true).execute().url();
            String rootUrl = UrlUtil.extractRootUrl(asUrl);
            this.visitedLinks = new VisitedLinks(rootUrl);
            url = UrlUtil.normalizeLink(rootUrl, asUrl.toString());
            PageNode root = PageNodeMapper.map(rootUrl, url);
            ArrayList<PageNode> toVisit = new ArrayList<PageNode>() {{
                add(root);
            }};
            this.toVisit = extractLinks(toVisit);
        } catch (IOException e) {
            this.visitedLinks = new VisitedLinks(url);
            onException(url, e);
        }
    }

    private CrawlerTask(Collection<PageNode> toVisit, VisitedLinks visitedLinks, int depth) {
        this.toVisit = toVisit;
        this.visitedLinks = visitedLinks;
        this.depth = depth;
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
        if (cannotFoundPageException(e)) {
            PageNode node = new PageNode(url, PageLinkType.INVALID_LINK);
            if (visitedLinks.notContains(url)) {
                visitedLinks.add(node);
                System.out.println("Cannot fetch url: " + node.getDomainUrl());
            }

        } else {
            e.printStackTrace();
        }
    }

    private boolean cannotFoundPageException(Exception e) {
        if (!(e instanceof HttpStatusException)) return false;
        int statusCode = ((HttpStatusException) e).getStatusCode();
        return IntStream.of(404, 403).anyMatch(code -> statusCode == code);
    }

    @Override
    protected Collection<PageNode> compute() {
        if (toVisit == null || toVisit.isEmpty() || depth <= 0) {
            return visitedLinks.nodes();
        }

        if (toVisit.size() > THRESHOLD) {
            List<PageNode> toVisitAsList = new ArrayList<>(toVisit);
            List<PageNode> firstPart = toVisitAsList.subList(0, toVisitAsList.size() / 2);
            List<PageNode> secondPart = toVisitAsList.subList(toVisitAsList.size() / 2, toVisitAsList.size());

            CrawlerTask left = new CrawlerTask(firstPart, visitedLinks, depth);
            CrawlerTask right = new CrawlerTask(secondPart, visitedLinks, depth);

            right.fork();
            left.compute();
            right.join();

        } else {
            Collection<PageNode> notVisited = extractLinks(toVisit);
            if (!notVisited.isEmpty()) {
                CrawlerTask cr = new CrawlerTask(notVisited, visitedLinks, depth - 1);
                cr.compute();
            }
        }

        return visitedLinks.nodes();
    }

}
