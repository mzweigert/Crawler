package com.mzweigert.crawler.model;

import com.mzweigert.crawler.util.UrlUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Set;
import java.util.stream.Collectors;

public class PageNodeMapper {

    private static final UrlValidator validator = new UrlValidator();

    public static Set<PageNode> map(String rootUrl, Set<PageNode> allVisited, Set<String> toMap) {
       return toMap.stream()
                .map(link -> mapToNode(rootUrl, link))
                .filter(node -> !allVisited.contains(node))
                .collect(Collectors.toSet());
    }

    private static PageNode mapToNode(String rootUrl, String link) {
        if (link.startsWith("/")) {
            link = rootUrl + link;
        } else if (link.charAt(link.length() - 1) == '/') {
            link = link.substring(0, link.length() - 1);
        }
        if(!validator.isValid(link)){
            return new PageNode(link, PageLinkType.INVALID_LINK);
        }

        if(!link.startsWith(rootUrl)){
            return new PageNode(link, PageLinkType.EXTERNAL_DOMAIN);
        }

        if(UrlUtil.extract(link).equals(rootUrl)){
            return new PageNode(link, PageLinkType.INTERNAL_ROOT_DOMAIN);
        }

        return new PageNode(link, PageLinkType.INTERNAL_SUB_DOMAIN);

    }
}
