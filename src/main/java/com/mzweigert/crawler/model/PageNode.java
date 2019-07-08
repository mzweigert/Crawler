package com.mzweigert.crawler.model;

import com.mzweigert.crawler.model.PageLinkType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        HashSet<PageNode> visited = new HashSet<>();

        return createString(visited, 0);
    }

    private String createString(Set<PageNode> visited, int depth) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < depth; i++){
            string.append(" ");
        }
        if(visited.contains(this)){
            string.append("- domainUrl : ").append(domainUrl).append(", type : {} ").append(type).append("\n");
        }
        visited.addAll(subLinks);
        string.append(", subLinks: \n");
        for (PageNode subLink : subLinks){
            String links = subLink.createString(visited, depth + 1);
            string.append(links);
        }
        return string.toString();
    }
}
