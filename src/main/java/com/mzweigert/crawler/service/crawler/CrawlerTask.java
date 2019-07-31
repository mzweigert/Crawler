package com.mzweigert.crawler.service.crawler;

import com.mzweigert.crawler.model.VisitedLinks;
import com.mzweigert.crawler.model.link.PageLink;
import com.mzweigert.crawler.model.link.PageLinkType;
import com.mzweigert.crawler.model.link.PageLinkMapper;
import com.mzweigert.crawler.util.AttributeFinder;
import com.mzweigert.crawler.util.UrlUtil;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrawlerTask extends RecursiveTask<Collection<PageLink>> {

    private Collection<PageLink> toVisit;
    private VisitedLinks visitedLinks;
    private int depth, threshold;

    CrawlerTask(String link, int maxDepth, int documentsPerWorker) {
        URL url = UrlUtil.asURL(link);
        if (url == null) {
            this.visitedLinks = new VisitedLinks(link);
            return;
        }

        String rootUrl = UrlUtil.extractRootUrl(url);
        this.visitedLinks = new VisitedLinks(rootUrl);
        link = UrlUtil.normalizeLink(rootUrl, url.toString());
        PageLink root = PageLinkMapper.map(rootUrl, link);
        this.toVisit = new ArrayList<PageLink>(1) {{
            add(root);
        }};
        this.depth = maxDepth;
        this.threshold = documentsPerWorker;
    }

    private CrawlerTask(Collection<PageLink> toVisit, VisitedLinks visitedLinks,
                        int depth, int documentsPerWorker) {
        this.toVisit = toVisit;
        this.visitedLinks = visitedLinks;
        this.depth = depth;
        this.threshold = documentsPerWorker;
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
                    Set<String> nodes = AttributeFinder.getInstance(connect.get()).find(notVisited);
                    notVisited.addAll(nodes);
                }
                visitedLinks.add(node);

            } catch (Exception e) {
                onException(node.getUrl(), e);
            }
        }
        return PageLinkMapper.mapMany(visitedLinks, notVisited);
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
        if (toVisit == null || toVisit.isEmpty() || depth <= 0) {
            return visitedLinks.nodes();
        }

        List<PageLink> toVisitFiltered = toVisit.stream()
                .filter(link -> !visitedLinks.contains(link.getUrl()))
                .collect(Collectors.toList());

        if (toVisitFiltered.size() > threshold) {
            List<PageLink> firstPart = toVisitFiltered.subList(0, toVisitFiltered.size() / 2);
            List<PageLink> secondPart = toVisitFiltered.subList(toVisitFiltered.size() / 2, toVisitFiltered.size());

            CrawlerTask left = new CrawlerTask(firstPart, visitedLinks, depth, threshold);
            CrawlerTask right = new CrawlerTask(secondPart, visitedLinks, depth, threshold);

            right.fork();
            left.compute();
            right.join();

        } else if(!toVisitFiltered.isEmpty()){
            Collection<PageLink> notVisited = extractLinks(toVisitFiltered);
            if (!notVisited.isEmpty()) {
                CrawlerTask task = new CrawlerTask(notVisited, visitedLinks, depth - 1, threshold);
                task.compute();
            }
        }

        return visitedLinks.nodes();
    }

}
