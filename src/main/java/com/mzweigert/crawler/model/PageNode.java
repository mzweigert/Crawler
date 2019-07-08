package com.mzweigert.crawler.model;

import java.util.Objects;

public class PageNode {

    private String domainUrl;

    private PageLinkType type;


    public PageNode(String domainUrl, PageLinkType type) {
        this.domainUrl = domainUrl;
        this.type = type;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public PageLinkType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageNode pageNode = (PageNode) o;
        return domainUrl.equals(pageNode.domainUrl) &&
                type == pageNode.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainUrl, type);
    }

    @Override
    public String toString() {
        return "PageNode{" +
                "domainUrl='" + domainUrl + '\'' +
                ", type=" + type +
                '}';
    }

}
