package com.mzweigert.crawler.model.node;

import java.util.Objects;

public class PageLink {

    private String url;
    private PageLinkType type;


    public PageLink(String url, PageLinkType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public PageLinkType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageLink)) return false;
        PageLink node = (PageLink) o;
        return Objects.equals(url, node.url) &&
                type == node.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, type);
    }

    @Override
    public String toString() {
        return "PageNode{" +
                "url='" + url + '\'' +
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
