package com.mzweigert.crawler.model;

import com.mzweigert.crawler.model.node.PageLink;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VisitedLinks {

    private String rootUrl;
    private Set<PageLink> visitedNodes;
    private Set<String> visitedLinks;

    public VisitedLinks(String rootUrl) {
        this.rootUrl = rootUrl;
        this.visitedNodes = ConcurrentHashMap.newKeySet();
        this.visitedLinks = ConcurrentHashMap.newKeySet();
    }

    public void add(PageLink node) {
        this.visitedLinks.add(node.getUrl());
        this.visitedNodes.add(node);
    }

    public Set<PageLink> nodes() {
        return visitedNodes;
    }

    public boolean contains(String url) {
        return visitedLinks.contains(url);
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public boolean notContains(String url) {
        return !contains(url);
    }
}
