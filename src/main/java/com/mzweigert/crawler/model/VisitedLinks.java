package com.mzweigert.crawler.model;

import com.mzweigert.crawler.model.node.PageNode;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VisitedLinks {

    private String rootUrl;
    private Set<PageNode> visitedNodes;
    private Set<String> visitedLinks;

    public VisitedLinks(String rootUrl) {
        this.rootUrl = rootUrl;
        this.visitedNodes = ConcurrentHashMap.newKeySet();
        this.visitedLinks = ConcurrentHashMap.newKeySet();
    }

    public void add(PageNode node) {
        this.visitedLinks.add(node.getDomainUrl());
        this.visitedNodes.add(node);
    }

    public Set<PageNode> nodes() {
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
