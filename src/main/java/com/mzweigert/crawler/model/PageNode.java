package com.mzweigert.crawler.model;

import java.util.Collection;
import java.util.HashSet;

public class PageNode {

    private String domainUrl;

    private PageLinkType type;

    private Collection<PageNode> subLinks;

    public PageNode(String domainUrl, PageLinkType type) {
        this.domainUrl = domainUrl;
        this.type = type;
        this.subLinks = new HashSet<>();
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public PageLinkType getType() {
        return type;
    }

    public Collection<PageNode> getSubLinks() {
        return subLinks;
    }
}
