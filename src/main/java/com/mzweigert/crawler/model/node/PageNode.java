package com.mzweigert.crawler.model.node;

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
        if (!(o instanceof PageNode)) return false;
        PageNode node = (PageNode) o;
        return Objects.equals(domainUrl, node.domainUrl) &&
                type == node.type;
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

    public boolean isInternalDomain() {
        return type.isOneOf(
                PageLinkType.INTERNAL_ROOT_DOMAIN,
                PageLinkType.INTERNAL_SUB_DOMAIN
        );
    }
}
